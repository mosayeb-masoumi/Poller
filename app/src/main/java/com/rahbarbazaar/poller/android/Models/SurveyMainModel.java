package com.rahbarbazaar.poller.android.Models;

public class SurveyMainModel {

    /**
     * id : 2
     * project : {"name":"test","description":"test","code":"123"}
     * currency : {"name":"پپول","value":100}
     * code : ijk-8876
     * title : عنوان
     * description : توضیحات
     * url : https://iransurveys.gide.net/cgi-bin/live/Q/fmc002fatab/fmc002fatab.pl
     * url_type: 2
     * start_date : 2018-12-26 07:40:00
     * end_date : 2018-12-26 13:23:00
     * point : 50
     * active : 1
     * status : 1
     */

    private int id;
    private ProjectBean project;
    private CurrencyBean currency;
    private String code;
    private String title;
    private String description;
    private String url;
    private String start_date;
    private String end_date;
    private String current_date;
    private String complete_date;
    private int point;
    private int active;
    private int status;
    private int url_type;
    private boolean isExpired;

    private String income;

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getComplete_date() {
        return complete_date;
    }

    public void setComplete_date(String complete_date) {
        this.complete_date = complete_date;
    }

    public int getUrl_type() {
        return url_type;
    }

    public void setUrl_type(int url_type) {
        this.url_type = url_type;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public String getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProjectBean getProject() {
        return project;
    }

    public void setProject(ProjectBean project) {
        this.project = project;
    }

    public CurrencyBean getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyBean currency) {
        this.currency = currency;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class ProjectBean {
        /**
         * name : test
         * description : test
         * code : 123
         */

        private String name;
        private String description;
        private String code;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class CurrencyBean {
        /**
         * name : پپول
         * value : 100
         */

        private String name;
        private int value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
