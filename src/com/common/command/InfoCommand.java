package com.common.command;

import com.common.Parsers;
import com.common.Request;
import com.common.exception.InvalidAmountOfArgumentsException;

public class InfoCommand extends Command{

    public InfoCommand() {
        super(CommandType.INFO);
    }

    @Override
    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 0);
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        }
        return getRequest();
    }
}
