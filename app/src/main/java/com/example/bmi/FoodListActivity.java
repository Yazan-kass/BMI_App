package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {

    private TextView back_home_foodList;

    private RecyclerViewFoodAdapter recyclerViewFoodAdapter;
    private RecyclerView recyclerViewFood;

    private List<Food> foodList;
    private String[] arrString;
    private String currentId;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        recyclerViewFood = findViewById(R.id.recyclerViewFood);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentId = firebaseAuth.getUid();

        back_home_foodList = findViewById(R.id.back_home_foodList);
        back_home_foodList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FoodListActivity.this, Home.class));
                finish();
            }
        });

        arrString = getResources().getStringArray(R.array.food_category);

        foodList = new ArrayList<>();

        db.collection("Food").whereEqualTo("userId", currentId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Food item = new Food();
                                item.setFoodName(document.getData().get("foodName").toString());
                                item.setCalorie(document.getData().get("calorie").toString());
                                item.setImg_URI(document.getData().get("img_URI").toString());
                                item.setCategory(document.getData().get("category").toString());
                                int selectedIdItem = Integer.parseInt(document.getData().get("selectedIdItem").toString());
                                item.setSelectedIdItem(selectedIdItem);
                                item.setFoodId(document.getId());


                                foodList.add(item);

                            }
                            recyclerViewFoodAdapter = new RecyclerViewFoodAdapter(getApplicationContext(), foodList);
                            recyclerViewFood.setHasFixedSize(true);
                            recyclerViewFood.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerViewFood.setAdapter(recyclerViewFoodAdapter);
                            recyclerViewFoodAdapter.notifyDataSetChanged();
                            recyclerViewFoodAdapter.OnItemCliclkLisener(new RecyclerViewFoodAdapter.ClickListener() {
                                @Override
                                public void onClick(Food result) {
                                    Intent intent = new Intent(getApplicationContext(), EditFoodActivity.class);
                                    intent.putExtra("foodName", result.getFoodName());
                                    intent.putExtra("calorie", result.getCalorie());
                                    intent.putExtra("img_URI", result.getImg_URI());
                                    intent.putExtra("foodId", result.getFoodId());
                                    intent.putExtra("category", result.getCategory());
                                    intent.putExtra("SelectedIdItem", result.getSelectedIdItem());
                                    startActivity(intent);
                                }
                            });

                        }
                    }
                });
    }
}