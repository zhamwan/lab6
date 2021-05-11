package com.common.model;

import java.io.Serializable;


public  class Flat implements Comparable <Flat>, Serializable {
    private static final long serialVersionUID = 7478347378541007490L;
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    //private java.util.Date creationDate = new Date(); //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int area; //Значение поля должно быть больше 0
    private int numberOfRooms; //Максимальное значение поля: 16, Значение поля должно быть больше 0
    private int floor; //Значение поля должно быть больше 0
    private Furnish furnish; //Поле не может быть null
    private Transport transport; //Поле не может быть null
    private House house; //Поле может быть null
    public Flat( int id, String name, Coordinates coordinates, int area, int numberOfRooms, int floor, Furnish furnish, Transport transport, House house) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.area = area;
        this.numberOfRooms = numberOfRooms;
        this.floor = floor;
        this.furnish = furnish;
        this.transport = transport;
        this.house = house;
    }



    @Override
    public int compareTo(Flat f) {
        return this.area - f.getArea();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getArea() {
        return area;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public int getFloor() {
        return floor;
    }

    public Furnish getFurnish() {
        return furnish;
    }

    public Transport getTransport() {
        return transport;
    }

    public House getHouse() {
        return house;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setFurnish(Furnish furnish) {
        this.furnish = furnish;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public void setHouse(House house) {
        this.house = house;
    }



    @Override
    public String toString() {
        return "Flat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", area=" + area +
                ", numberOfRooms=" + numberOfRooms +
                ", floor=" + floor +
                ", furnish=" + furnish +
                ", transport=" + transport +
                ", house=" + house +
                '}';
    }
}