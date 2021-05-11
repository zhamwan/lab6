package com.common.model;


import java.io.Serializable;

/**
 * Coordinates data class
 */

public enum Transport implements Serializable {
    FEW,
    NONE,
    LITTLE,
    NORMAL,
    ENOUGH;

    public static String nameList() {
        StringBuilder nameList = new StringBuilder();
        for (Transport transport: values()) {
            nameList.append(transport.name()).append(", ");
        }
        return nameList.substring(0, nameList.length() - 2);
    }
}