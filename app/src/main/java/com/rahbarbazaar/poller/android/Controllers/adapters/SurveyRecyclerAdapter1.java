package com.rahbarbazaar.poller.android.Controllers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rahbarbazaar.poller.android.Controllers.viewHolders.SurveyHolder1;
import com.rahbarbazaar.poller.android.Controllers.viewHolders.SurveyItemInteraction;
import com.rahbarbazaar.poller.android.Models.SurveyMainModel;
import com.rahbarbazaar.poller.android.R;

import java.util.List;


public class SurveyRecyclerAdapter1 extends RecyclerView.Adapter<SurveyHolder1> {

    private List<SurveyMainModel> items;

    public SurveyRecyclerAdapter1(List<SurveyMainModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SurveyHolder1 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new SurveyHolder1(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.survey_rv_items1, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyHolder1 holder, int i) {
        //bind survey data and listener
        SurveyMainModel data = items.get(i);
        holder.bindSurveyData(data,i);
        holder.setOnSurveyHolderListener(listener,data);
    }


    private SurveyItemInteraction listener = null;

    public void setListener(SurveyItemInteraction listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
