package com.server;

import com.common.model.*;
import com.common.exception.InsufficientPermissionException;
import com.common.exception.InvalidDBOutputException;

import java.sql.*;
import java.time.LocalDate;
import java.util.TreeMap;


public class DatabaseHandler {

    private static final String pepper = "2Hq@*!8fdAQl";
    private static final String ADD_USER_REQUEST = "INSERT INTO USERS (username, password) VALUES (?, ?)";
    private static final String VALIDATE_USER_REQUEST = "SELECT COUNT(*) AS count FROM USERS WHERE username = ? AND password = ?";
    private static final String FIND_USERNAME_REQUEST = "SELECT COUNT(*) AS count FROM USERS WHERE username = ?";
    private static final String INSERT_FLAT_REQUEST = "INSERT INTO FLATS (id, key, nameflat , x, y,  area, numberofrooms, floor," +
            "namehouse, year, numberofloors, furnish, transport, owner) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String JOIN_FLAT_REQUEST = "SELECT * FROM flats";
    private static final String GET_MAX_FLAT_ID_REQUEST = "SELECT MAX(id) AS id FROM flats";
    private static final String CHECK_ID_PRESENT_REQUEST = "SELECT COUNT(*) AS count FROM FLATS WHERE id = ?";
    private static final String CHECK_KEY_PRESENT_REQUEST = "SELECT COUNT(*) AS count FROM FLATS WHERE key = ?";
    private static final String REMOVE_BY_FLAT_KEY_REQUEST = "DELETE FROM FLATS WHERE key = ?";
    private static final String FLAT_BY_OWNER_REQUEST = "SELECT (*) FROM FLATS WHERE owner = ?";
    private static final String IS_OWNER_REQUEST = "SELECT COUNT(*) FROM FLATS WHERE key = ? AND owner = ?";
    private static final String UPDATE_FLAT_REQUEST = "UPDATE FLATS SET " +
            "nameFlat = ?, " +
            "x = ?, " +
            "y = ?, " +
            "area = ?, " +
            "numberOfRooms = ?, " +
            "floor = ?, " +
            "nameHouse = ?, " +
            "year = ? " +
            "numberofloors = ? "+
            "furnish = ? "+
            "transport= ? "+
            "owner = ? "+
            "WHERE key = ?";


    private String URL;
    private String username;
    private String password;
    private Connection connection;


    public DatabaseHandler(String URL, String username, String password) {
        this.URL = URL;
        this.username = username;
        this.password = password;
    }

    public void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            System.out.println("Подключение к базе данных установлено.");
        } catch (SQLException e) {
            System.err.println("Не удалось выполнить подключение к базе данных. Завершение работы." + e );
            System.exit(-1);
        }
    }

    public TreeMap<Integer, Flat> loadCollectionFromDB() {
        TreeMap<Integer, Flat> collection = new TreeMap<>();
        try {
            PreparedStatement joinStatement = connection.prepareStatement(JOIN_FLAT_REQUEST);
            ResultSet result = joinStatement.executeQuery();

            while (result.next()) {
                try {
                    int key = extractKeyFromResult(result);
                    Flat f = extractFlatFromResult(result);
                    collection.put(key, f);
                } catch (InvalidDBOutputException e) {
                    System.out.println("Неверный объект");
                    continue;
                }

            }

            joinStatement.close();
            System.out.println("Коллекция успешно загружена из базы данных. Количество элементов: " + collection.size());
        } catch (SQLException e) {
            System.out.println("Произошла ошибка при загрузке коллекции из базы данных. Завершение работы.");
            e.printStackTrace();
            System.exit(-1);
        }
      //  System.out.println(collection);
        return collection;
    }

    private int extractKeyFromResult(ResultSet result) throws SQLException{
        return result.getInt("key");
    }


    private Flat extractFlatFromResult(ResultSet result) throws SQLException, InvalidDBOutputException {
        int id = result.getInt("id");
        if (id < 1) throw new InvalidDBOutputException();
        String flatName = result.getString("nameFlat");
        if (flatName == null || flatName.isEmpty()) throw new InvalidDBOutputException();
        int x = result.getInt("x");
        float y = result.getFloat("y");
        int area = result.getInt("area");
        Furnish furnish = Furnish.valueOf(result.getString("furnish"));
        Transport transport = Transport.valueOf(result.getString("transport"));
        int floor = result.getInt("floor");
        int numberOfRooms = result.getInt("numberOfRooms");
        String nameHouse = result.getString("nameHouse");
        int year = result.getInt("year");
        int numberofloors = result.getInt("numberofloors");
        House house = new House(nameHouse, year, numberofloors);
        Coordinates coordinates = new Coordinates(x, y);
        Flat flat = new Flat(id, flatName, coordinates, area, floor, numberOfRooms,  furnish, transport, house);
        return flat;
    }

    public boolean insertFlat(Flat f, int key, String owner) {
        String name = f.getName();
        Coordinates coordinates = f.getCoordinates();
        int area = f.getArea();
        int floor = f.getFloor();
        int numberOfRooms = f.getNumberOfRooms();
        Furnish furnish = f.getFurnish();
        Transport transport = f.getTransport();
        House house = f.getHouse();
        try {
            connection.setAutoCommit(false);
            connection.setSavepoint();

            PreparedStatement addToFlatsStatement = connection.prepareStatement(INSERT_FLAT_REQUEST);
            addToFlatsStatement.setInt(1, getNextFlatId());
            addToFlatsStatement.setInt(2, key);
            addToFlatsStatement.setString(3, name);
            addToFlatsStatement.setInt(4, coordinates.getX());
            addToFlatsStatement.setDouble(5,coordinates.getY());
            addToFlatsStatement.setInt(6, area);
            addToFlatsStatement.setInt(7, numberOfRooms);
            addToFlatsStatement.setInt(8, floor);
            addToFlatsStatement.setString(9, house.getName());
            addToFlatsStatement.setInt(10, house.getYear());
            addToFlatsStatement.setInt(11, house.getNumberOfFloors());
            addToFlatsStatement.setString(12, furnish.toString());
            addToFlatsStatement.setString(13, transport.toString());
            addToFlatsStatement.setString(14, owner);
            addToFlatsStatement.executeUpdate();
            addToFlatsStatement.close();
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            rollback();
        }

        return false;
    }
    public boolean updateFlat(Flat f, int key, String owner) {
        String name = f.getName();
        Coordinates coordinates = f.getCoordinates();
        int area = f.getArea();
        int floor = f.getFloor();
        int numberOfRooms = f.getNumberOfRooms();
        Furnish furnish = f.getFurnish();
        Transport transport = f.getTransport();
        House house = f.getHouse();
        try {
            connection.setAutoCommit(true);
            connection.setSavepoint();
            PreparedStatement addToFlatsStatement = connection.prepareStatement(UPDATE_FLAT_REQUEST);
            addToFlatsStatement.setString(1, name);
            addToFlatsStatement.setInt(2, coordinates.getX());
            addToFlatsStatement.setFloat(3, coordinates.getY());
            addToFlatsStatement.setInt(4, area);
            addToFlatsStatement.setInt(5, numberOfRooms);
            addToFlatsStatement.setInt(6, floor);
            addToFlatsStatement.setString(7, house.getName());
            addToFlatsStatement.setInt(8, house.getYear());
            addToFlatsStatement.setInt(9, house.getNumberOfFloors());
            addToFlatsStatement.setString(10, furnish.toString());
            addToFlatsStatement.setString(11, transport.toString());
            addToFlatsStatement.setString(12, owner);
            addToFlatsStatement.executeUpdate();
            addToFlatsStatement.close();
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            rollback();
        }

        return false;
    }

    public boolean removeFlatKey(int key , String initiator) throws SQLException, InsufficientPermissionException {
        if (!checkKeyExistence(key)) return false;
        if (!isOwnerOf(key, initiator)) throw new InsufficientPermissionException();
        PreparedStatement removeFlatStatement = connection.prepareStatement(REMOVE_BY_FLAT_KEY_REQUEST);
        connection.setAutoCommit(false);
        connection.setSavepoint();
        removeFlatStatement.setInt(1, key);
        removeFlatStatement.executeUpdate();
        removeFlatStatement.close();
        connection.commit();
        connection.setAutoCommit(true);
        return true;
    }

    public boolean checkIdExistence(int id) throws SQLException {
        PreparedStatement checkId = connection.prepareStatement(CHECK_ID_PRESENT_REQUEST);
        checkId.setInt(1, id);
        ResultSet resultSet = checkId.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) == 0) return false;
        else return true;
    }

    public boolean checkKeyExistence(int key) throws SQLException {
        PreparedStatement checkId = connection.prepareStatement(CHECK_KEY_PRESENT_REQUEST);
        checkId.setInt(1, key);
        ResultSet resultSet = checkId.executeQuery();
        resultSet.next();
        if (resultSet.getInt(1) == 0) return false;
        else return true;
    }
    public int getNextFlatId() {
        try {
            PreparedStatement getMaxId = connection.prepareStatement(GET_MAX_FLAT_ID_REQUEST);
            ResultSet result = getMaxId.executeQuery();
            if (result.next()) return result.getInt("id")+1;
            else return 1;
        } catch (SQLException e) {
            System.out.println("Ошибка генерации id");
        }
        return 1;
    }


    public boolean registerUser(String username, String password) throws SQLException {
        if (userExists(username)) return false;
        PreparedStatement addStatement = connection.prepareStatement(ADD_USER_REQUEST);
        addStatement.setString(1, username);
        addStatement.setString(2, com.server.DataHasher.encryptStringMD2(password + pepper));
        addStatement.executeUpdate();
        addStatement.close();
        return true;
    }

    public boolean validateUser(String username, String password) throws SQLException {
        PreparedStatement findUserStatement = connection.prepareStatement(VALIDATE_USER_REQUEST);
        String hashedPassword = com.server.DataHasher.encryptStringMD2(password + pepper);
        findUserStatement.setString(1, username);
        findUserStatement.setString(2, hashedPassword);
        ResultSet result = findUserStatement.executeQuery();
        result.next();
        int count = result.getInt(1);
        findUserStatement.close();
        if (count == 1) return true;
        return false;
    }

    public boolean userExists(String username) throws SQLException {
        PreparedStatement findStatement = connection.prepareStatement(FIND_USERNAME_REQUEST);
        findStatement.setString(1, username);
        ResultSet result = findStatement.executeQuery();
        result.next();
        int count = result.getInt(1);
        findStatement.close();
        if (count == 1) return true;
        return false;
    }

    public boolean isOwnerOf(int key, String username) throws SQLException {
        PreparedStatement ownerStatement = connection.prepareStatement(IS_OWNER_REQUEST);
        ownerStatement.setInt(1, key);
        ownerStatement.setString(2, username);
        ResultSet result = ownerStatement.executeQuery();
        result.next();
        System.out.println(result);
        if (result.getInt(1) == 1) return true;
        return false;
    }

    public void rollback() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Не удалось откатить изменения.");
        }
    }
}
