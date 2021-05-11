package com.common.command;

import com.common.Parsers;
import com.common.Request;
import com.common.exception.DomainViolationException;
import com.common.exception.InvalidAmountOfArgumentsException;

public class RemoveKeyCommand extends Command{
    private int key;

    public RemoveKeyCommand() {
        super(CommandType.REMOVE_KEY);
    }


    @Override
    public Request execute(String[] commandSplit) {
        try {
            Parsers.verify(commandSplit, 1);
            int k = Parsers.parseKey(commandSplit[1]);
            this.key = k;
            return getRequest();
        } catch (InvalidAmountOfArgumentsException e) {
            e.printMessage();
        } catch (DomainViolationException dve) {
            dve.printMessage();
        }

        return new Request(null);
    }

    public int getVenue() {
        return this.key;
    }
}