package com.common.exception;

public class InvalidAuthTypeException extends Exception{
    public InvalidAuthTypeException(String message) {
        super (message);
    }

    public void printMessage() {
        System.out.println(this.getMessage());
    }
}
