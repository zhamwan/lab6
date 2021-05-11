package com.client;

import com.common.CommandProcessor;

public class ClientMain {
    public static void main(String[] args) {
        Client client = new Client("localhost", 11321, new CommandProcessor());
        client.start();
    }
}