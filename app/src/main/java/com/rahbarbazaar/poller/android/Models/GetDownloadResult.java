package com.rahbarbazaar.poller.android.Models;

public class GetDownloadResult {

    /**
     *
     * created by hamed at 97/11/04
     *
     * {
     *   version : 4.0.1
     *   url : something for download
     *   play_url: something
     *   bazaar_url : something
     * }
     *
     */

    private String version,url,bazaar_url,play_url,force_update;
//    private String version,url,bazaar_url,play_url;

    public String getBazaar_url() {
        return bazaar_url;
    }

    public void setBazaar_url(String bazaar_url) {
        this.bazaar_url = bazaar_url;
    }

    public String getPlay_url() {
        return play_url;
    }

    public void setPlay_url(String play_url) {
        this.play_url = play_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getForce_update() {
        return force_update;
    }

    public void setForce_update(String force_update) {
        this.force_update = force_update;
    }
}
