package com.common.command;

import com.common.Parsers;
import com.common.Request;
import com.common.model.*;
import com.common.exception.InvalidAmountOfArgumentsException;
import com.common.Updater;

import java.util.NoSuchElementException;

public class RemoveGreaterCommand extends Command {
    private Flat flat;

    public RemoveGreaterCommand() {
        super(CommandType.REMOVE_GREATER);
    }

    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 0);
            String nameFlat = Updater.updateName();
            Coordinates coordinates = Updater.updateCoordinates();
            Furnish furnish = Updater.updateFurnish();
            Transport transport = Updater.updateTransport();
            int area = Updater.updateArea();
            int floor = Updater.updateFloor();
            int numberOfRooms = Updater.updateNumberOfRooms();
            String nameHouse = Updater.updateName();
            int year = Updater.updateYear();
            int numberOfFloors = Updater.updateNumberOfFloors();
            House house = new House(nameHouse, year, numberOfFloors);
            Flat f = new Flat(-1, nameFlat, coordinates, area, floor, numberOfRooms, furnish, transport, house);
            this.flat = f;
            return getRequest();
        } catch (NoSuchElementException e) {
            System.out.println("Получен сигнал конца ввода. Завершение.");
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат данных.");
        }
        return new Request(null);
    }

    public Flat getTicket() {
        return flat;
    }
}
