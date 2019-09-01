package com.rahbarbazaar.poller.android.Models.getimages;

import com.google.gson.annotations.SerializedName;

public class GetImagesDetail {

    @SerializedName("news")
    public String news;
    @SerializedName("video")
    public String video;
    @SerializedName("survey")
    public String survey;
    @SerializedName("balance")
    public Integer balance;
    @SerializedName("score")
    public Integer score;
    @SerializedName("activeSurveys")
    public Integer activeSurveys;
    @SerializedName("lottery_days")
    public Integer lotteryDays;

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getSurvey() {
        return survey;
    }

    public void setSurvey(String survey) {
        this.survey = survey;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getActiveSurveys() {
        return activeSurveys;
    }

    public void setActiveSurveys(Integer activeSurveys) {
        this.activeSurveys = activeSurveys;
    }

    public Integer getLotteryDays() {
        return lotteryDays;
    }

    public void setLotteryDays(Integer lotteryDays) {
        this.lotteryDays = lotteryDays;
    }
}
