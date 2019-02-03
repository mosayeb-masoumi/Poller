package com.rahbarbazaar.poller.android.Controllers.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import com.rahbarbazaar.poller.android.Controllers.viewHolders.SurveyHolder;
import com.rahbarbazaar.poller.android.Controllers.viewHolders.SurveyItemInteraction;
import com.rahbarbazaar.poller.android.Models.SurveyMainModel;
import com.rahbarbazaar.poller.android.R;

public class SurveyRecyclerAdapter extends RecyclerView.Adapter<SurveyHolder> {

    private List<SurveyMainModel> items;

    public SurveyRecyclerAdapter(List<SurveyMainModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SurveyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new SurveyHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.survey_rv_items, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyHolder holder, int i) {

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
