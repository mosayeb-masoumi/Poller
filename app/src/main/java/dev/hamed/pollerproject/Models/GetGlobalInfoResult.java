package dev.hamed.pollerproject.Models;

public class GetGlobalInfoResult {

    /**
     * force_update : {"status":true,"url":"test.com"}
     * date : 1397-09-24
     */

    private ForceUpdateBean force_update;
    private String date;

    public ForceUpdateBean getForce_update() {
        return force_update;
    }

    public void setForce_update(ForceUpdateBean force_update) {
        this.force_update = force_update;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static class ForceUpdateBean {
        /**
         * status : true
         * url : test.com
         */

        private boolean status;
        private String url;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
