package com.rahbarbazaar.poller.Models;

import java.util.List;

public class GetSurveyHistoryListResult {

    private List<SurveyMainModel> actives;
    private List<SurveyMainModel> expired;

    public List<SurveyMainModel> getActives() {
        return actives;
    }

    public void setActives(List<SurveyMainModel> actives) {
        this.actives = actives;
    }

    public List<SurveyMainModel> getExpired() {
        return expired;
    }

    public void setExpired(List<SurveyMainModel> expired) {
        this.expired = expired;
    }

}
