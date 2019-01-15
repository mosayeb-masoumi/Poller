package dev.hamed.pollerproject.Models;

public class GetTransactionResult {


    /**
     * id : 75
     * user_id : 2
     * transaction_amount : 3000
     * transactionable_id : 4
     * transactionable_type : survey
     * created_at : 2019-01-08 05:30:00
     * updated_at : 2019-01-08 05:30:00
     * transactionable : {"id":4,"project_id":1,"currency_id":1,"survey_code":"edk-65888","survey_title":"نظرسنجی ۳","survey_description":"نظرسنجی مواد شوینده","survey_url":"http://46.209.107.34:8309/onlinesurvey/index.php/985538","survey_start_date":"2018-12-20 12:00:00","survey_end_date":"2018-12-24 18:00:00","survey_point":3000,"survey_active":1,"created_at":"2018-12-15 07:15:00","updated_at":"2018-12-15 07:15:00"}
     */

    private int id;
    private int user_id;
    private String transaction_amount;
    private int transactionable_id;
    private String transactionable_type;
    private String created_at;
    private String updated_at;
    private TransactionableBean transactionable;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(String transaction_amount) {
        this.transaction_amount = transaction_amount;
    }

    public int getTransactionable_id() {
        return transactionable_id;
    }

    public void setTransactionable_id(int transactionable_id) {
        this.transactionable_id = transactionable_id;
    }

    public String getTransactionable_type() {
        return transactionable_type;
    }

    public void setTransactionable_type(String transactionable_type) {
        this.transactionable_type = transactionable_type;
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

    public TransactionableBean getTransactionable() {
        return transactionable;
    }

    public void setTransactionable(TransactionableBean transactionable) {
        this.transactionable = transactionable;
    }

    public static class TransactionableBean {
        /**
         * id : 4
         * project_id : 1
         * currency_id : 1
         * survey_code : edk-65888
         * survey_title : نظرسنجی ۳
         * title : شارز
         * survey_description : نظرسنجی مواد شوینده
         * survey_url : http://46.209.107.34:8309/onlinesurvey/index.php/985538
         * survey_start_date : 2018-12-20 12:00:00
         * survey_end_date : 2018-12-24 18:00:00
         * survey_point : 3000
         * survey_active : 1
         * created_at : 2018-12-15 07:15:00
         * updated_at : 2018-12-15 07:15:00
         */

        private int id;
        private int project_id;
        private int currency_id;
        private String survey_code;
        private String survey_title;
        private String title;
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

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
