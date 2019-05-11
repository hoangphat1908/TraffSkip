package com.example.myapplication;

public class Floor {
    private String name;
    private int remaining;
    private int total;
    private String occupancyImage;
    public Floor() {}
    public Floor(String name, int remaining, int total) {
        this.name = name;
        this.remaining = remaining;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getOccupancyImage() {
        return occupancyImage;
    }

    public void setOccupancyImage(String occupancyImage) {
        this.occupancyImage = occupancyImage;
    }
}
