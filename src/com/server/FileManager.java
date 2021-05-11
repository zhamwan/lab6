package com.server;

import com.common.exception.NoAccessToFileException;
import com.common.model.*;
import com.server.CollectionManager;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.TreeMap;

/**
 * A class used for working with files. Provides XML read and write operations. The file is specified with environment variable.
 */
public class FileManager {
    public CollectionManager collectionManager;
    private TreeMap<Integer, Flat> collection;
    private String envVar;
    private boolean isRead = false;

    public FileManager(TreeMap<Integer, Flat> collection,String envVarName) {
        this.collection = collection;
        this.envVar = System.getenv(envVarName);
        if (envVar == null) {
            System.out.println("Предупреждение: не найдена переменная окружения, содержащая путь к xml файлу.");
        }
    }

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public void setCollection(TreeMap<Integer, Flat> collection) {
        this.collection = collection;
    }

    /**
     * Saves the collection into XML-file specified in envVar.
     */
    public void saveCollectionToFile() {
        if (envVar != null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            Document document = db.newDocument();
            Element root = document.createElement("collection");
            document.appendChild(root);

            // append the document with tickets
            for (Integer i: collection.keySet()) {
                createXMLTicketStructure(i, document);
            }

            // create the xml file
            //transform the DOM Object to an XML File
            transformIntoFile(document);


        } else {
            System.out.println("Ошибка записи. Переменная окружения с путём к файлу не найдена.");
            // System.out.println(System.getenv());
        }

    }

    /**
     * Creates an XML-structure via DOM-parser for the specified ticket.
     * @param i - a ticket to create a structure for
     * @param document - a document the structure is created in.
     */
    public void createXMLTicketStructure(Integer i, Document document) {
        // root
        Element flatRoot = document.createElement("flat");
        document.getFirstChild().appendChild(flatRoot);

        // key
        Attr attrKey = document.createAttribute("key");
        attrKey.setValue(String.valueOf(i));
        flatRoot.setAttributeNode(attrKey);
        // id
        Attr attrId = document.createAttribute("id");
        attrId.setValue(String.valueOf(collection.get(i).getId()));
        flatRoot.setAttributeNode(attrId);

        // name
        Element name = document.createElement("name");
        name.appendChild(document.createTextNode(collection.get(i).getName()));
        flatRoot.appendChild(name);
        // coordinates
        Element coordinates = document.createElement("coordinates");
        Coordinates c = collection.get(i).getCoordinates();
        Element x = document.createElement("x");
        Element y = document.createElement("y");
        x.appendChild(document.createTextNode(String.valueOf(c.getX())));
        y.appendChild(document.createTextNode(String.valueOf(c.getY())));
        coordinates.appendChild(x);
        coordinates.appendChild(y);
        flatRoot.appendChild(coordinates);
        //area
        Element area = document.createElement("area");
        area.appendChild(document.createTextNode(String.valueOf(collection.get(i).getArea())));
        flatRoot.appendChild(area);
        //numberOfRooms
        Element numberOfRooms = document.createElement("numberOfRooms");
        numberOfRooms.appendChild(document.createTextNode(String.valueOf(collection.get(i).getNumberOfRooms())));
        flatRoot.appendChild(numberOfRooms);
        //floor
        Element floor = document.createElement("floor");
        floor.appendChild(document.createTextNode(String.valueOf(collection.get(i).getFloor())));
        flatRoot.appendChild(floor);
        //furnish
        Element furnish = document.createElement("furnish");
        furnish.appendChild(document.createTextNode(collection.get(i).getFurnish().name()));
        flatRoot.appendChild(furnish);
        //furnish
        Element transport = document.createElement("transport");
        transport.appendChild(document.createTextNode(collection.get(i).getTransport().name()));
        flatRoot.appendChild(transport);
        // house
        Element house = document.createElement("house");
        House h = collection.get(i).getHouse();
        Element hName = document.createElement("name");
        Element hYear = document.createElement("year");
        Element hNumberOfFloors = document.createElement("numberOfFloors");
        hName.appendChild(document.createTextNode(h.getName()));
        hYear.appendChild(document.createTextNode(String.valueOf(h.getYear())));
        hNumberOfFloors.appendChild(document.createTextNode(String.valueOf(h.getNumberOfFloors())));
        house.appendChild(hName);
        house.appendChild(hYear);
        house.appendChild(hNumberOfFloors);
        flatRoot.appendChild(house);
    }

    /**
     * Transforms a document object to a real XML-file on the machine.
     * @param document - a document object to transform into file
     */
    public void transformIntoFile(Document document) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            FileOutputStream buffOutStr = getOutStr();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(buffOutStr);
            transformer.transform(domSource, streamResult);
            System.out.println("Запись успешна.");
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка записи. Файл по указанному в переменной окружения пути не найден.");
        } catch (TransformerException e) {
            System.out.println("Непредвиденная ошибка конфигурации.");
        } catch (NoAccessToFileException e) {
            System.out.println("нет доступа к файлу");
        }
    }

    /**
     * Reads all the tickets from the XML-file.
     * @return the ArrayList with Tickets parsed.
     */
    public TreeMap<Integer, Flat> parseCollectionFromFile() {
        TreeMap<Integer, Flat> treeMap = new TreeMap<>();
        Flat flat;

        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = f.newDocumentBuilder();
            Document document = db.parse(getInStr());
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("flat");

            for (int index = 0; index < nodeList.getLength(); index++) {
                Node node = nodeList.item(index);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    int key = Integer.parseInt(e.getAttribute("key"));
                    int id = Integer.parseInt(e.getAttribute("id"));
                    String name = e.getElementsByTagName("name").item(0).getTextContent();
                    int area = Integer.parseInt(e.getElementsByTagName("area").item(0).getTextContent());
                    int floor = Integer.parseInt(e.getElementsByTagName("floor").item(0).getTextContent());
                    int numberOfRooms =  Integer.parseInt(e.getElementsByTagName("numberOfRooms").item(0).getTextContent());
                    Furnish furnish = Furnish.valueOf(e.getElementsByTagName("furnish").item(0).getTextContent());
                    Transport transport = Transport.valueOf(e.getElementsByTagName("transport").item(0).getTextContent());
                    Element coordinates = (Element) (e.getElementsByTagName("coordinates").item(0));
                    Coordinates coordinates1 = new Coordinates(Integer.parseInt(coordinates.getElementsByTagName("x").item(0).getTextContent()),
                            Float.parseFloat(coordinates.getElementsByTagName("y").item(0).getTextContent()));
                    Element h = (Element) (e.getElementsByTagName("house").item(0));
                    House house = new House(h.getElementsByTagName("name").item(0).getTextContent(), Integer.parseInt(h.getElementsByTagName("year").item(0).getTextContent()), Integer.parseInt(h.getElementsByTagName("numberOfFloors").item(0).getTextContent()));
                    flat = new Flat(id, name, coordinates1, area, numberOfRooms, floor, furnish, transport, house);

                    treeMap.put(key, flat);
                }
            }
            isRead = true;
        } catch (IOException e) {
            System.out.println("Ошибка чтения.");
        } catch (NoAccessToFileException e) {
            System.out.println("Нет доступа к файлу.");
        } catch (ParserConfigurationException e) {
            System.out.println("Ошибка конфигурации парсера.");
        } catch (SAXException e) {
            System.out.println("Ошибка парсинга. Проверьте структуру XML-файла.");
        } catch (NumberFormatException e) {
            System.out.println("Ошибка парсинга. Проверьте правильность введенных данных.");
        } catch (NullPointerException e) {
            System.out.println("Ошибка парсинга. Проверьте, что файл существует и все необходимые поля заполнены.");
        }
        System.out.println("Объектов загружено: " + treeMap.size());
        return treeMap;
    }


    public FileOutputStream getOutStr() throws FileNotFoundException, NoAccessToFileException {
        File file = new File(envVar);
        if (file.exists() && !file.canWrite()) throw new NoAccessToFileException();
        return new FileOutputStream(file);
    }


    public BufferedInputStream getInStr() throws FileNotFoundException, NoAccessToFileException {
        File file = new File(envVar);
        if (file.exists() && !file.canRead()) throw new NoAccessToFileException();
        return new BufferedInputStream(new FileInputStream(file));
    }

    public boolean isRead() {
        return isRead;
    }
}