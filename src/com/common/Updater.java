package com.common;

import com.common.model.*;
import com.common.exception.DomainViolationException;
import com.common.exception.InvalidAmountOfArgumentsException;

import java.util.Scanner;


public class Updater {



    public static Coordinates updateCoordinates() {
        while (true) {
            System.out.println("Укажите координаты x, y через пробел (x < 750): ");
            String[] data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim().split(" ");
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim().split(" ");
            }
            try {
                if (data.length != 2) throw new InvalidAmountOfArgumentsException(2);
                int x = Integer.parseInt(data[0]);
                float y = Float.parseFloat(data[1]);
                if (!(x < 750)) throw new DomainViolationException("x > 750 required");
                if (!(x < Integer.MAX_VALUE)) throw new DomainViolationException("x is out of 'int' type bounds");
                if (!((y < Float.MAX_VALUE) && (y > Float.MIN_VALUE))) throw new DomainViolationException("y is out of 'float' type bounds");
                return new Coordinates(x, y);
            } catch (NumberFormatException nfe) {
                System.out.println("Неверный формат. Ожидается два числа x и y формата int и float соответственно (x < 750).");
            } catch (DomainViolationException dve) {
                System.out.println("Неверный формат. Координата должна быть x < 750.");
            } catch (InvalidAmountOfArgumentsException e) {
                System.out.println("Ожидается два аргумента: x и у через пробел.");
            }
        }
    }

    public static Furnish updateFurnish() {
        while (true) {
            System.out.println("Выберите тип Furnish из предложенных: ");
            for (Furnish type: Furnish.values()) {
                System.out.println(type);
            }
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim().toUpperCase();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim().toUpperCase();
            }
            if (data.isEmpty()) {
                return null;
            }
            else {
                try {
                    return Furnish.valueOf(data);
                } catch (IllegalArgumentException iae) {
                    System.out.println("Неверный ввод.");
                }
            }
        }
    }
    public static Transport updateTransport() {
        while (true) {
            System.out.println("Выберите тип Furnish из предложенных: ");
            for (Transport type: Transport.values()) {
                System.out.println(type);
            }
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim().toUpperCase();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim().toUpperCase();
            }
            if (data.isEmpty()) {
                return null;
            }
            else {
                try {
                    return Transport.valueOf(data);
                } catch (IllegalArgumentException iae) {
                    System.out.println("Неверный ввод.");
                }
            }
        }
    }

    public static String updateName() {
        while (true) {
            System.out.print("Введите имя: ");
            String data;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                data = CommandProcessor.fileScanner.nextLine().trim();
                System.out.println(data);
            }
            if (data.isEmpty()) System.out.println("Строка не может быть пустой");
            else {
                return data;
            }
        }
    }

    public static int updateArea() {
        while (true) {
            System.out.print("Введите area: ");
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim();
            }
            try {
                int area = Integer.parseInt(data);
                if (!(area > 0)) throw new DomainViolationException("Area должна быть больше нуля.");
                return area;
            } catch (DomainViolationException e) {
                e.printMessage();
            } catch (NumberFormatException nfe) {
                System.out.println("Неверный формат. Ожидается число формата int.");
            }
        }
    }
    public static int updateNumberOfRooms() {
        while (true) {
            System.out.print("Введите number Of Rooms: ");
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim();
            }
            try {
                int nb = Integer.parseInt(data);
                if (!(nb > 0) && !(nb < 17)) throw new DomainViolationException("number Of Rooms должна быть больше нуля и меньше 17.");
                return nb;
            } catch (DomainViolationException e) {
                e.printMessage();
            } catch (NumberFormatException nfe) {
                System.out.println("Неверный формат. Ожидается число формата int.");
            }
        }
    }
    public static int updateFloor() {
        while (true) {
            System.out.print("Введите floor: ");
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim();
            }
            try {
                int floor = Integer.parseInt(data);
                if (!(floor > 0) ) throw new DomainViolationException("floor должна быть больше нуля.");
                return floor;
            } catch (DomainViolationException e) {
                e.printMessage();
            } catch (NumberFormatException nfe) {
                System.out.println("Неверный формат. Ожидается число формата int.");
            }
        }
    }
    public static int updateYear() {
        while (true) {
            System.out.print("Введите year: ");
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim();
            }
            try {
                int year = Integer.parseInt(data);
                if (!(year > 0) ) throw new DomainViolationException("year должна быть больше нуля.");
                return year;
            } catch (DomainViolationException e) {
                e.printMessage();
            } catch (NumberFormatException nfe) {
                System.out.println("Неверный формат. Ожидается число формата int.");
            }
        }
    }
    public static int updateNumberOfFloors() {
        while (true) {
            System.out.print("Введите numberOfFloors: ");
            String data;
            String output;
            if (!CommandProcessor.fileMode) data = new Scanner(System.in).nextLine().trim();
            else {
                output = CommandProcessor.fileScanner.nextLine();
                System.out.println(output);
                data = output.trim();
            }
            try {
                int numberOfFloors = Integer.parseInt(data);
                if (!(numberOfFloors > 0) ) throw new DomainViolationException("numberOfFloors должна быть больше нуля.");
                return numberOfFloors;
            } catch (DomainViolationException e) {
                e.printMessage();
            } catch (NumberFormatException nfe) {
                System.out.println("Неверный формат. Ожидается число формата int.");
            }
        }
    }


}
