package com.example.smartcards;

import java.util.ArrayList;

public class Folder {
    String name;
    int id;

    public Folder (int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}