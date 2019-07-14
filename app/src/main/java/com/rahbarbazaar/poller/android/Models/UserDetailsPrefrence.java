package com.rahbarbazaar.poller.android.Models;

public class UserDetailsPrefrence {

    private String name;
    private String type;
    private int balance;
    private int sum_points;
    private int sum_score;
    private int score;
    private String identity;
    private String phone_number;
    private String user_id;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSum_score() {
        return sum_score;
    }

    public void setSum_score(int sum_score) {
        this.sum_score = sum_score;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public int getSum_points() {
        return sum_points;
    }

    public void setSum_points(int sum_points) {
        this.sum_points = sum_points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
