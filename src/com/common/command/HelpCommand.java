package com.common.command;

import com.common.Parsers;
import com.common.Request;
import com.common.exception.InvalidAmountOfArgumentsException;

public class HelpCommand extends Command {

    public HelpCommand() {
        super(CommandType.HELP);
    }


    @Override
    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 0);
            return getRequest();
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        }
        return new Request(null);
    }
}
