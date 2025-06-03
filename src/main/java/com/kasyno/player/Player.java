package com.kasyno.player;

public class Player {
    private double balance;
    private String id;

    public Player(String id) {
        this.id = id;
        this.balance = 1000;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

}
