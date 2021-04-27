package com.common.command;

import com.common.Parsers;
import com.common.Request;
import com.common.model.*;
import com.common.exception.InvalidAmountOfArgumentsException;
import com.common.Updater;

public class CountByTransportCommand extends Command{
    private Transport transport;

    public CountByTransportCommand() {
        super(CommandType.COUNT_BY_TRANSPORT);
    }


    @Override
    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 0);
            this.transport = Updater.updateTransport();
            return getRequest();
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        }
        return new Request(null);
    }

    public Transport getVenue() {
        return this.transport;
    }
}