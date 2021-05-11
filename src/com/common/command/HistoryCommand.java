package com.common.command;

import com.common.Parsers;
import com.common.Request;
import com.common.exception.InvalidAmountOfArgumentsException;

public class HistoryCommand extends Command {

    public HistoryCommand() {
        super(CommandType.HISTORY);
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
