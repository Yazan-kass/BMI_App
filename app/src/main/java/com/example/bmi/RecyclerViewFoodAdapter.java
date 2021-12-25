package com.example.bmi;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class RecyclerViewFoodAdapter extends RecyclerView.Adapter<RecyclerViewFoodAdapter.ViewHolder> {
    private Context context;
    private List<Food> foodList;
    FirebaseFirestore db;
    private static ClickListener listener;

    public RecyclerViewFoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.food_list_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        holder.foodName.setText(foodList.get(position).getFoodName());
        holder.foodType.setText(foodList.get(position).getCategory());
        holder.calories.setText(foodList.get(position).getCalorie());
        Glide.with(context).load(foodList.get(position).getImg_URI()).into(holder.imageView_Food);
        int newPosition = position;
        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("Food").document(foodList.get(newPosition).getFoodId()).delete().
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Successfully Removed", Toast.LENGTH_SHORT).show();
                                    foodList.remove(newPosition);
                                    context.startActivity(new Intent(context, FoodListActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                }
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName;
        public TextView foodType;
        public TextView calories;
        public ImageView imageView_Food;
        public Button btn_Edit;
        public Button btn_Delete;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            foodName = itemView.findViewById(R.id.foodName);
            foodType = itemView.findViewById(R.id.foodType);
            calories = itemView.findViewById(R.id.calories);
            imageView_Food = itemView.findViewById(R.id.imageView_Food);
            btn_Delete = itemView.findViewById(R.id.btn_Delete);
            btn_Edit = itemView.findViewById(R.id.btn_Edit);
            btn_Edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(foodList.get(getAdapterPosition()));
                }
            });

        }

    }


    public void OnItemCliclkLisener(ClickListener listener) {
        RecyclerViewFoodAdapter.listener = listener;
    }

    public interface ClickListener {
        void onClick(Food result);
    }
}

