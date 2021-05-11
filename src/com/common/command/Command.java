package com.common.command;

import com.common.Request;

import java.io.Serializable;

public abstract class Command implements Serializable {
    protected final CommandType commandType;
    public abstract Request execute(String[] commandSplit);

    public CommandType getCommandType() {
        return commandType;
    }



    protected Command(CommandType commandType) {
        this.commandType = commandType;
    }


    public Request getRequest() {
        Request req = new Request(this);
        return req;
    }

}
