package dev.hamed.pollerproject.Models;

public class GetPagesResult {

    /**
     * id : 11
     * title : تست تایتل
     * title_icon : http://46.209.107.34:2296/webservice/public/images/icon/icon/home.png
     * url : http://46.209.107.34:2296/webservice//page/11
     * content : <p dir="rtl"><strong>عنوان تست</strong></p>

     <p dir="rtl"><em>این متن تست است ..........</em></p>

     <p dir="rtl">فقط برای تست فقط برای تست فقط برای تست فقط برای تست فقط برای تست فقط برای تست فقط برای تست فقط برای تست فقط برای تست فقط برای تست فقط برای تست فقط برای تست فقط برای تست فقط برای تست فقط برای تست فقط برای تست</p>
     */

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
