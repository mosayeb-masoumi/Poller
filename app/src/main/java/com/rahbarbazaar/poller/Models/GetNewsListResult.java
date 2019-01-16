package com.rahbarbazaar.poller.Models;

import java.util.List;

public class GetNewsListResult {

    /**
     * current_page : 1
     * data : [{"id":1,"title":"rahbar bazaar","link":"test","image":"http://admin.rahbarbazaar.com/Uploaded/Banner/1bee945c-406f-42c7-b360-5d8a70f80d24.jpg"},{"id":3,"title":"rahbar bazaar","link":"test","image":"http://admin.rahbarbazaar.com/Uploaded/Banner/7a40e172-6612-4d0a-bcfc-3ce32fb61100.jpg"},{"id":4,"title":"rahbar bazaar","link":"test","image":"http://admin.rahbarbazaar.com/Uploaded/Banner/f71f8327-1f96-43da-a801-26471de336d5.jpg"},{"id":5,"title":"rahbar bazaar","link":"test","image":"http://admin.rahbarbazaar.com/Uploaded/SiteMenuItem/Editor/Retail-Audit.jpg"},{"id":6,"title":"rahbar bazaar","link":"test","image":"http://admin.rahbarbazaar.com/Uploaded/Banner/db95557d-472a-4013-9bcd-f40451b9023f.png"}]
     * first_page_url : http://46.209.107.34:2296/webservice/public/v1/news?page=1
     * from : 1
     * last_page : 5
     * last_page_url : http://46.209.107.34:2296/webservice/public/v1/news?page=5
     * next_page_url : http://46.209.107.34:2296/webservice/public/v1/news?page=2
     * path : http://46.209.107.34:2296/webservice/public/v1/news
     * per_page : 5
     * prev_page_url : null
     * to : 5
     * total : 21
     */

    private String first_page_url;
    private String next_page_url;
    private String path;
    private String prev_page_url;
    private String last_page_url;
    private int per_page;
    private int to;
    private int total;
    private int current_page;
    private int from;
    private int last_page;
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
         * title : rahbar bazaar
         * link : test
         * image : http://admin.rahbarbazaar.com/Uploaded/Banner/1bee945c-406f-42c7-b360-5d8a70f80d24.jpg
         */

        private int id;
        private String title;
        private String link;
        private String image;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
