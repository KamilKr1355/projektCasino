package com.kasyno.gry;

public  class Card {
    private String suit;
    private String rank;
    private int value;

    public Card(String suit, String rank, int value) {
        this.suit = suit;
        this.rank = rank;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return rank + " of " + suit;
    }

    public boolean isAce() {
        return rank.equals("Ace");
    }
}
