package com.common.model;


import java.io.Serializable;

/**
 * Coordinates data class
 */



public class House  implements Serializable {
    private static final long serialVersionUID = 3888450949293392826L;
    private String name; //Поле не может быть null
    private int year; //Поле не может быть null, Значение поля должно быть больше 0
    private int numberOfFloors; //Поле может быть null, Значение поля должно быть больше 0

    public House(String name, int year, int numberOfFloors) {
        this.name = name;
        this.year = year;
        this.numberOfFloors = numberOfFloors;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setNumberOfFloors(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
    }

    @Override
    public String toString() {
        return "House{" +
                "name='" + name + '\'' +
                ", year=" + year +
                ", numberOfFloors=" + numberOfFloors +
                '}';
    }
}