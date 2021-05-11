package com.server;

import com.common.Response;
import com.common.model.Flat;
import com.common.model.Furnish;
import com.common.model.Transport;

import com.common.exception.*;
import java.sql.SQLException;
import java.util.*;

/**
 * Operates the com.commands.
 */

public  class CollectionManager {
    private int nextId;
    private TreeMap<Integer, Flat> collection;
    private static HashMap<String, String> description;
    private String listType;
    private final Date initDate;
    private FileManager fileManager;
    private DatabaseHandler dbHandler;
    public CollectionManager(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }
    {
        collection = new TreeMap<Integer, Flat>();
        initDate = new Date();
        description = new HashMap<String, String>();
        listType = collection.getClass().getSimpleName();
       // fileManager = new FileManager(collection, "envXML");
        //load();
        //nextId = collection.size();
        description.put("help", "вывести справку по доступным командам"); // done
        description.put("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д."); // done
        description.put("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении"); // done
        description.put("insert", "добавить новый элемент в коллекцию "); // done
        description.put("update", "обновить значение элемента коллекции, id которого равен заданному "); // done
        description.put("remove_key", "удалить элемент из коллекции по его key |"); // done
        description.put("clear", "очистить коллекцию"); // done
        description.put("save", "сохранить коллекцию в файл"); // done
        description.put("execute_script", "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме "); // done
        description.put("exit", "завершить без сохранения в файл"); // done
        description.put("replace_if_lowe {element}", " заменить значение по ключу, если новое значение меньше старого"); // done
        description.put("remove_greater {element}", "удалить из коллекции все элементы, превышающие заданный"); // done
        description.put("remove_any_by_furnish furnish", " удалить из коллекции один элемент, значение поля furnish которого эквивалентно заданному"); // done
        description.put("remove_all_by_furnish furnish", " удалить из коллекции все элементы, значение поля furnish которого эквивалентно заданному"); // done
        description.put("count_by_transport transport", " посчитать кол-во транспорта "); // donee
    }
    public void init() {
        collection = dbHandler.loadCollectionFromDB();
        listType = collection.getClass().getSimpleName();
       // System.out.println(collection);
    }
    public void setTreeMap(TreeMap<Integer, Flat> treeMap) {
        this.collection = treeMap;
    }

    public int getId() {
        return nextId;
    }

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void setId(int id) {
        this.nextId = id;
    }

    public Date getDate() {
        return initDate;
    }


    public TreeMap<Integer, Flat> getTreeMap() {
        return collection;
    }


    public Response help() {
        StringBuilder msg = new StringBuilder();
        Set keySet = description.keySet();
        keySet.stream().forEach(key -> msg.append(key + " - " + description.get(key) + "\n"));
        //System.out.println(msg.toString());
        return new Response(msg.toString());
        // TODO: how to send this to a client? maybe make this client-side?
    }
    public String toString() {
        return "Тип коллекции: " + listType + "\nДата инициализации: " + initDate + "\nКоличество объектов: " + collection.size();
    }

    public synchronized Response insert(Flat flat, int key){
        flat.setId(generateNextId());
        if (!checkKey(key)){
        collection.put(key, flat);
            return new Response("Добавлен объект: " + flat.toString());
        }
        return new Response("ключ уже занят");
    }
    /**
     * remove element in collection
     * @param key
     */

    public synchronized Response removeKey(int key){
        if (checkKey(key)){
            collection.remove(key);
            return new Response("элемент удален");}
        return new Response("такого ключа нет");
        }

    /**
     *
     display all elements of the collection
     */
    public Response showCollection() {
        StringBuilder s = new StringBuilder();
        collection.keySet().stream().forEach( key -> s.append(key + collection.get(key).toString() + "\n"));
        return new Response(s.toString());
    }
    /**
     *
     display info of the collection
     */
    public Response getInfo() {
        return new Response(toString());
    }
    /**
     clear collection
     */
    public Response clear(){
        collection.clear();
        return new Response("коллекция успешно очищена");
    }
    /**
     *
     remove all elements of the collection,
     in which the value of the furnish field is equal to the given
     */
 /*   public Response removeAllByFurnish(Furnish furnish){
        long l = 0;
        l = collection.keySet().stream().filter(i -> collection.get(i).getFurnish()==furnish).count();
        collection.keySet().stream().filter(i -> collection.get(i).getFurnish()==furnish).forEach(i ->
                collection.remove(i));
        return new Response("удалено " + l + "объектов");
    }*/
    public synchronized Response removeAllByFurnish(Furnish furnish, String initiator) {
        StringBuilder sb = new StringBuilder();
        ArrayList<Flat> temp = new ArrayList(collection.keySet());
        ArrayList<Integer> keySet = new ArrayList<>();
        Iterator it = temp.iterator();
        while (it.hasNext()) {
            int key = (Integer) it.next();
            Flat t = (Flat) collection.get(key);
            if (t.getFurnish() == furnish) {
                try {
                    dbHandler.removeFlatKey(key, initiator);
                    keySet.add(key);
                    sb.append("Удален объект: " + t.toString() + "\n");
                } catch (InsufficientPermissionException e) {
                    sb.append("Объект с id " + t.getId() + " не был удален: недостаточно прав.\n");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        for( int i=0; i<keySet.size(); i++){
            collection.remove(keySet.get(i));
        }
        return new Response(sb.toString());
    }
    /**
     *
     remove 1 elements of the collection,
     in which the value of the furnish field is equal to the given
     */
    public synchronized Response removeAnyByFurnish(Furnish furnish, String initiator) {
        StringBuilder sb = new StringBuilder();
        ArrayList<Flat> temp = new ArrayList(collection.keySet());
        Iterator it = temp.iterator();
        while (it.hasNext()) {
            int key = (Integer) it.next();
            Flat t = (Flat) collection.get(key);
            if (t.getFurnish() == furnish) {
                try {
                    dbHandler.removeFlatKey(key, initiator);
                    collection.remove(key);
                    sb.append("Удален объект: " + t.toString() + "\n");
                } catch (InsufficientPermissionException e) {
                    sb.append("Объект с id " + t.getId() + " не был удален: недостаточно прав.\n");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            }

        }
        return new Response(sb.toString());
    }
 /*   public Response removeAnyByFurnish(Furnish furnish){
        int found = collection.keySet().stream().filter(i -> collection.get(i).getFurnish() == furnish).findFirst().orElse(-1);
        if (found == -1){
            return new Response("объект с заданным furnish не найден");
        }
        collection.remove(found);
        return new Response("объект с заданным furnish удален");
    }*/
    /**
     * Count elements in collection by transport.
     */
    public Response countByTransport(Transport transport){
        long l = collection.keySet().stream().filter(i -> collection.get(i).getTransport() == transport).count();
        return new Response("найденно " + l + " транспортов");
    }

    /**
     *
     remove all elements the value of the area field is greater than the specified one
     * @param flat
     */
  /*  public Response removeGreater(Flat flat){
        long l = 0;
        l = collection.keySet().stream().filter(i -> collection.get(i).compareTo(flat)>0).count();
        collection.keySet().stream().filter(i -> collection.get(i).compareTo(flat)>0).forEach(i ->
            collection.remove(i));
            return new Response("удаленно " + l + " объектов" );
    }*/

    public synchronized Response removeGreater(Flat flat, String initiator) {
        StringBuilder sb = new StringBuilder();
            ArrayList<Flat> temp = new ArrayList(collection.keySet());
            Iterator it = temp.iterator();
            ArrayList<Integer> keySet = new ArrayList<>();
            while (it.hasNext()) {
                int key = (Integer) it.next();
                Flat t = (Flat) collection.get(key);
                if (t.compareTo(flat) > 0) {
                    try {
                        dbHandler.removeFlatKey(key, initiator);
                        keySet.add(key);
                        sb.append("Удален объект: " + t.toString() + "\n");
                    } catch (InsufficientPermissionException e) {
                        sb.append("Объект с id " + t.getId() + " не был удален: недостаточно прав.\n");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }
            for( int i=0; i<keySet.size(); i++){
                collection.remove(keySet.get(i));
            }
            return new Response(sb.toString());
    }
    /**
     * update element
     * @param flat
     */
    public synchronized Response update( int key, Flat flat){
        if (checkKey(key)){
            collection.remove(key);
            collection.put(key, flat);
            return new Response("объект успешно обнавлен");
        }
        return new Response("объект не найден");
    }
    public boolean checkId(int id){
        if(collection.keySet().stream().filter(i -> i==id ).count() == 0){
       // for (Integer i : collection.keySet()){
        //    if (collection.get(i).getId() == id){
                return true; }
        return false;
    }

    /**
     * replace if area value is less than specified
     * @param key
     * @param flat
     * @return
     */
    public synchronized Response replaceIfLowe(int key, Flat flat, String initir){
        if (flat.compareTo(collection.get(key))>0){
            flat.setId(collection.get(key).getId());
            dbHandler.updateFlat(flat, key, initir);
            collection.remove(key);
            collection.put(key, flat);
            return new Response("объект заменен");
        }
        return new Response("объект не заменен");}
    public boolean checkKey(int key){
        return collection.containsKey(key);
    }
    public boolean checkFlat(Flat flat){
        return collection.containsValue(flat);
    }
    public boolean checkEmpty(){
        return collection.size()>0;
    }
    public int collectionSize(){
        return collection.size();
    }
    /**
     * generating the next id
     */
    public Integer generateNextId(){
        return ++nextId;
    }

    /**
     * save collection in xml
     */
    public void saveToFile(){
        fileManager.saveCollectionToFile();
    }
    /**
     * read collection in xml
     */
    public void load() {
        this.collection = fileManager.parseCollectionFromFile();
        fileManager.setCollection(collection);
    }

    public int getId(int key){
        if(checkKey(key)){
            return collection.get(key).getId();
        }
        return 0;
    }
}