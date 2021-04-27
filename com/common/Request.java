package com.common;

import com.common.command.Command;

import java.io.Serializable;

public class Request implements Serializable {

    private static final long serialVersionUID = -6213323027290265345L;
    private Command command;

    public Request(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return this.command;
    }

    public boolean isEmpty() {
        return command == null;
    }

}
