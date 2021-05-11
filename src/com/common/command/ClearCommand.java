package com.common.command;

import com.common.Parsers;
import com.common.Request;
import com.common.exception.InvalidAmountOfArgumentsException;

public class ClearCommand extends Command {

    public ClearCommand() {
        super(CommandType.CLEAR);
    }

    public Request execute(String[] commandSplit) {
        try{
            Parsers.verify(commandSplit, 0);
            return getRequest();
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        }
        return null;
    }
}
