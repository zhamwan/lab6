package com.common.command;

import com.common.Parsers;
import com.common.Request;
import com.common.exception.InvalidAmountOfArgumentsException;

public class ShowCommand extends Command {

    public ShowCommand() {
        super(CommandType.SHOW);
    }

    @Override
    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 0);
            return getRequest();
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        }
        return null;
    }
}
