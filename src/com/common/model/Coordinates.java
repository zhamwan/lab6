package com.common.model;
import java.io.Serializable;



import java.util.Objects;

/**
 * Coordinates data class
 */

public class Coordinates implements  Serializable {
    private static final long serialVersionUID = -5107483809114366253L;
    private int x;
    private float y;

    public Coordinates() {}

    /**
     * @param x - X coordinate
     * @param y - Y coordinate
     */
    public Coordinates(int x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * @return Y coordinate
     */
    public float getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return y == that.y &&
                Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}