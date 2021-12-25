package com.example.bmi;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.List;


public class RecyclerViewRecordsAdapter extends Adapter<RecyclerViewRecordsAdapter.ViewHolder> {

    private Context context;
    private List<Records> recordsList;

    public RecyclerViewRecordsAdapter(Context context, List<Records> recordsList) {
        this.context = context;
        this.recordsList = recordsList;
    }

    @NonNull
    @Override
    public RecyclerViewRecordsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.element, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewRecordsAdapter.ViewHolder viewHolder, int position) {

        viewHolder.weight.setText(recordsList.get(position).getWeight());
        viewHolder.length.setText(recordsList.get(position).getLength());
        viewHolder.state.setText(recordsList.get(position).getState());
        viewHolder.date.setText(recordsList.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return recordsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView weight;
        public TextView length;
        public TextView date;
        public TextView state;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            weight = itemView.findViewById(R.id.textview_weight);
            length = itemView.findViewById(R.id.textview_length);
            date = itemView.findViewById(R.id.textview_date);
            state = itemView.findViewById(R.id.textview_state);

        }

    }

}
