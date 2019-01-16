package com.rahbarbazaar.poller.Models;

import java.util.List;

public class GetSurveyListResult {

    /**
     * current_page : 1
     * data : [{"id":1,"project_id":2,"currency_id":3,"survey_code":"234","survey_title":"test","survey_description":"fsdfd","survey_url":"","survey_start_date":"0000-00-00 00:00:00","survey_end_date":"0000-00-00 00:00:00","survey_point":0,"survey_active":1,"created_at":null,"updated_at":null},{"id":2,"project_id":2,"currency_id":1,"survey_code":"2","survey_title":"1","survey_description":"","survey_url":"","survey_start_date":"0000-00-00 00:00:00","survey_end_date":"0000-00-00 00:00:00","survey_point":0,"survey_active":1,"created_at":null,"updated_at":null}]
     * first_page_url : http://46.209.107.34:2296/webservice/public/v1/survey/latest?page=1
     * from : 1
     * last_page : 1
     * last_page_url : http://46.209.107.34:2296/webservice/public/v1/survey/latest?page=1
     * next_page_url : null
     * path : http://46.209.107.34:2296/webservice/public/v1/survey/latest
     * per_page : 10
     * prev_page_url : null
     * to : 2
     * total : 2
     */

    private int current_page;
    private String first_page_url;
    private int from;
    private int last_page;
    private String last_page_url;
    private String next_page_url;
    private String path;
    private int per_page;
    private String prev_page_url;
    private int to;
    private int total;
    private List<DataBean> data;

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public String getFirst_page_url() {
        return first_page_url;
    }

    public void setFirst_page_url(String first_page_url) {
        this.first_page_url = first_page_url;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public String getLast_page_url() {
        return last_page_url;
    }

    public void setLast_page_url(String last_page_url) {
        this.last_page_url = last_page_url;
    }

    public String getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(String next_page_url) {
        this.next_page_url = next_page_url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public String getPrev_page_url() {
        return prev_page_url;
    }

    public void setPrev_page_url(String prev_page_url) {
        this.prev_page_url = prev_page_url;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * project_id : 2
         * currency_id : 3
         * survey_code : 234
         * survey_title : test
         * survey_description : fsdfd
         * survey_url :
         * survey_start_date : 0000-00-00 00:00:00
         * survey_end_date : 0000-00-00 00:00:00
         * survey_point : 0
         * survey_active : 1
         * created_at : null
         * updated_at : null
         */

        private int id;
        private int project_id;
        private int currency_id;
        private String survey_code;
        private String survey_title;
        private String survey_description;
        private String survey_url;
        private String survey_start_date;
        private String survey_end_date;
        private int survey_point;
        private int survey_active;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getProject_id() {
            return project_id;
        }

        public void setProject_id(int project_id) {
            this.project_id = project_id;
        }

        public int getCurrency_id() {
            return currency_id;
        }

        public void setCurrency_id(int currency_id) {
            this.currency_id = currency_id;
        }

        public String getSurvey_code() {
            return survey_code;
        }

        public void setSurvey_code(String survey_code) {
            this.survey_code = survey_code;
        }

        public String getSurvey_title() {
            return survey_title;
        }

        public void setSurvey_title(String survey_title) {
            this.survey_title = survey_title;
        }

        public String getSurvey_description() {
            return survey_description;
        }

        public void setSurvey_description(String survey_description) {
            this.survey_description = survey_description;
        }

        public String getSurvey_url() {
            return survey_url;
        }

        public void setSurvey_url(String survey_url) {
            this.survey_url = survey_url;
        }

        public String getSurvey_start_date() {
            return survey_start_date;
        }

        public void setSurvey_start_date(String survey_start_date) {
            this.survey_start_date = survey_start_date;
        }

        public String getSurvey_end_date() {
            return survey_end_date;
        }

        public void setSurvey_end_date(String survey_end_date) {
            this.survey_end_date = survey_end_date;
        }

        public int getSurvey_point() {
            return survey_point;
        }

        public void setSurvey_point(int survey_point) {
            this.survey_point = survey_point;
        }

        public int getSurvey_active() {
            return survey_active;
        }

        public void setSurvey_active(int survey_active) {
            this.survey_active = survey_active;
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
}
