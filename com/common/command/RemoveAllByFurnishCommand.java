package com.common.command;

import com.common.Parsers;
import com.common.Request;
import com.common.model.*;
import com.common.exception.InvalidAmountOfArgumentsException;
import com.common.Updater;

public class RemoveAllByFurnishCommand extends Command{
    private Furnish furnish;

    public RemoveAllByFurnishCommand() {
        super(CommandType.REMOVE_ALL_BY_FURNISH);
    }


    @Override
    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 0);
            this.furnish = Updater.updateFurnish();
            return getRequest();
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        }
        return new Request(null);
    }

    public Furnish getVenue() {
        return this.furnish;
    }
}