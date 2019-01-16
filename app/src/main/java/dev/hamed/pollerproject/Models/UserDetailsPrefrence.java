package dev.hamed.pollerproject.Models;

public class UserDetailsPrefrence {

    private String name;
    private String user_id;
    private int balance;
    private int sum_points;
    private String identity;

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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
