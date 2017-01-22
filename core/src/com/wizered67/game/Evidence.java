package com.wizered67.game;

/**
 * Created by Adam on 1/21/2017.
 */
public class Evidence {
    private String name;
    public String description;
    public int id;
    public String info;
    public Evidence(String n, String d, int i, String link) {
        name = n;
        description = d;
        id = i;
        info = link;
    }
    @Override
    public String toString() {
        return name;
    }
}
