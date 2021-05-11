package com.server;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) {
        Scanner credentials = null;
        String username = null;
        String password = null;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Не найден POSTGRESQL драйвер.");
            System.exit(-1);
        }

        try {
            credentials = new Scanner(new FileReader("credentials.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("Не найден credentials.txt с данными для входа в базу данных.");
            System.exit(-1);
        }
        //String jdbcURL = "jdbc:postgresql://localhost:5675/studs";
        String jdbcURL = "jdbc:postgresql://pg:5432/studs";
        try {
            username = credentials.nextLine().trim();
            password = credentials.nextLine().trim();
        } catch (NoSuchElementException e) {
            System.err.println("Не найдены данные для входа в файле. Завершение работы.");
            System.exit(-1);
        }

        DatabaseHandler dbHandler = new DatabaseHandler(jdbcURL, username, password);
        CollectionManager cm = new CollectionManager(dbHandler);
        dbHandler.connectToDatabase();
        cm.init();
        com.server.Server connection = new com.server.Server(11321, new ServerInputHandler(cm), new ServerRequestHandler(cm, dbHandler));
        connection.start();
    }
}
