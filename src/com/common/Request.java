package com.common;

import com.common.command.Command;

import java.io.Serializable;

public class Request implements Serializable {

    private static final long serialVersionUID = -6213323027290265345L;
    private Command command;
    private String token;
    private String initiator;

    public Request(Command command) {
        this.command = command;
    }

    public Request(Command command, String token) {
        this.command = command;
        this.token = token;
    }

    public Command getCommand() {
        return this.command;
    }

    public boolean isEmpty() {
        return command == null;
    }

    public String getToken() {
        return token;
    }

    public void addToken(String token) {
        this.token = token;
    }

    public void addInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getInitiator() {
        return initiator;
    }

}
