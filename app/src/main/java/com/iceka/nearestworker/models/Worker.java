package com.iceka.nearestworker.models;

public class Worker {

    private String id;
    private String name;
    private String services;
    private int phone_number;
    private int rating;
    private int closed;
    private double lat;
    private double lng;

    public Worker() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getServices() {
        return services;
    }

    public int getPhone_number() {
        return phone_number;
    }

    public int getRating() {
        return rating;
    }

    public int getClosed() {
        return closed;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
