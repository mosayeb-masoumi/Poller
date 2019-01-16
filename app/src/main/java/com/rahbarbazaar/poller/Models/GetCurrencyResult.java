package com.rahbarbazaar.poller.Models;

public class GetCurrencyResult {

    /**
     * id : 1
     * currency_name : تومان
     * currency_value : 100
     * created_at : 2018-12-28 12:12:35
     * updated_at : 2018-12-28 12:12:37
     */

    private int id;
    private String currency_name;
    private int currency_value;
    private String created_at;
    private String updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public void setCurrency_name(String currency_name) {
        this.currency_name = currency_name;
    }

    public int getCurrency_value() {
        return currency_value;
    }

    public void setCurrency_value(int currency_value) {
        this.currency_value = currency_value;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
