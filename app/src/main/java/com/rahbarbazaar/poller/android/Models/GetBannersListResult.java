package com.rahbarbazaar.poller.android.Models;

public class GetBannersListResult {

    /**
     * id : 1
     * title : poller
     * link : poller
     * image : http://46.209.107.34:2296/poller/public/media/poller1.jpg
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
