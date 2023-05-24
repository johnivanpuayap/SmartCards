package com.example.smartcards;

public class Card {

    int id;
    String front;
    String back;
    int rating;

    public Card(int id, String front, String back, int rating) {
        this.id = id;
        this.front = front;
        this.back = back;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public String getBack() {
        return back;
    }

    public String getFront() {
        return front;
    }

    public int getRating() {
        return rating;
    }
}
