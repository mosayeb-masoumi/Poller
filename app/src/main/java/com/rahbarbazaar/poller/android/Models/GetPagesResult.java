package com.rahbarbazaar.poller.android.Models;

public class GetPagesResult {

    private int id;
    private String title;
    private String title_icon;
    private String url;
    private String content;

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

    public String getTitle_icon() {
        return title_icon;
    }

    public void setTitle_icon(String title_icon) {
        this.title_icon = title_icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
