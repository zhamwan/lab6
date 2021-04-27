package com.common.model;

/**
 * Furnish enum class. Contains 4 values.
 */


import java.io.Serializable;

/**
 * Coordinates data class
 */

public  enum Furnish implements Serializable {
    NONE,
    FINE,
    BAD,
    LITTLE;


    public static String nameList() {
        StringBuilder nameList = new StringBuilder();
        for (Furnish furnish: values()) {
        nameList.append(furnish.name()).append(", ");
        }
        return nameList.substring(0, nameList.length() - 2);
    }
}