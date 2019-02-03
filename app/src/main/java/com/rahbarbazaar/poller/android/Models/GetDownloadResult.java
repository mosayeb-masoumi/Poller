package com.rahbarbazaar.poller.android.Models;

public class GetDownloadResult {

    /**
     *
     * created by hamed at 97/11/04
     *
     * {
     *   version : 4.0.1
     *   url : something for download
     * }
     *
     */

    private String version,url;

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
}
