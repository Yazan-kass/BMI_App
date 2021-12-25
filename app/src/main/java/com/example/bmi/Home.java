package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Home extends AppCompatActivity {
    private Button button_addFood;
    private Button button_addRecord;
    private Button button_viewFood;
    private TextView textview_currentState;
    private TextView logout;
    private String gender, firestore_weight, firestore_length, firestore_state, dob, state;
    private int year, age;
    private Calendar calendar;
    private double newWeight, newLength, newAge, newBMI, bmiBefore, bmiAfter;

    private List<Records> recordsList;

    private RecyclerViewRecordsAdapter recyclerViewRecordsAdapter;
    private RecyclerView recyclerViewRecords;

    private String currentId;


    private FirebaseAuth firebaseAuth;

    //Connection to Firebase FireStore
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView detsUserName = findViewById(R.id.textview_userName);
        recyclerViewRecords = findViewById(R.id.recyclerViewRecords);
        textview_currentState = findViewById(R.id.textview_currentState);

        calendar = Calendar.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentId = firebaseAuth.getUid();

        recordsList = new ArrayList<>();

        db.collection("Users").document(currentId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            detsUserName.setText("Hi, " + task.getResult().get("username").toString());
                            dob = task.getResult().get("DOB").toString();
                        }
                    }
                });


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewUserRecords();
            }
        }, 1000);

        button_addRecord = findViewById(R.id.button_add_record);
        button_addRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, New_Record.class);
                startActivity(i);
            }
        });
        logout = findViewById(R.id.textView_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        button_addFood = findViewById(R.id.button_add_food);
        button_addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, AddFood.class);
                startActivity(i);
            }
        });

        button_viewFood = findViewById(R.id.button_view_food);
        button_viewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, FoodListActivity.class));
            }
        });


    }

    private void viewUserRecords() {
        db.collection("Records").whereEqualTo("userId", currentId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String firestore_date = document.getData().get("date").toString();
                                firestore_weight = document.getData().get("weight").toString();
                                firestore_length = document.getData().get("length").toString();
                                firestore_state = document.getData().get("state").toString();
                                String firestore_bmi = document.getData().get("bmi").toString();

                                Records newRecords = new Records();
                                newRecords.setDate(firestore_date);
                                newRecords.setWeight(firestore_weight);
                                newRecords.setLength(firestore_length);
                                newRecords.setUserId(currentId);
                                newRecords.setState(firestore_state);
                                newRecords.setBmi(firestore_bmi);
                                recordsList.add(newRecords);

                            }

                            Collections.sort(recordsList);
                            Collections.reverse(recordsList);

                            if (recordsList.size() == 1) {
                                year = Integer.parseInt(dob.substring(7).toString());
                                age = calendar.get(Calendar.YEAR) - year;
                                newWeight = Double.parseDouble(firestore_weight);
                                newLength = Math.pow(Double.parseDouble(firestore_length) / 100, 2);

                                bmiCalc();
                                if (newBMI > 30) {
                                    state = "Obesity";
                                    textview_currentState.setText(state);
                                } else if (newBMI >= 25 && newBMI < 30) {
                                    state = "Overweight";
                                    textview_currentState.setText(state);
                                } else if (newBMI >= 18.5 && newBMI < 25) {
                                    state = "Healthy Weight";
                                    textview_currentState.setText(state);
                                } else if (newBMI < 18.5) {
                                    state = "Underweight";
                                    textview_currentState.setText(state);
                                }
                            }

                            if (recordsList.size() > 1) {

                                bmiBefore = Double.parseDouble(recordsList.get(0).getBmi());
                                bmiAfter = Double.parseDouble(recordsList.get(1).getBmi());
                                year = Integer.parseInt(dob.substring(7).toString());
                                age = calendar.get(Calendar.YEAR) - year;
                                newWeight = Double.parseDouble(firestore_weight);
                                newLength = Math.pow(Double.parseDouble(firestore_length) / 100, 2);
                                bmiCalc();

                                Double changedBMI = bmiBefore - bmiAfter;
                                String beforeState = recordsList.get(0).getState();

                                switch (beforeState) {
                                    case "Obesity":
                                        if ((changedBMI < -1) || (changedBMI >= -1 && changedBMI < -0.6)) {
                                            textview_currentState.setText("Obesity Go Ahead");
                                        } else if ((changedBMI < -0.3 && changedBMI >= -0.6) || (changedBMI >= -0.3 && changedBMI < 0)) {
                                            textview_currentState.setText("Obesity Little Changes");
                                        } else if (changedBMI >= 0 && changedBMI < 0.3) {
                                            textview_currentState.setText("Obesity Be Careful");
                                        } else if ((changedBMI >= 0.3 && changedBMI < 0.6) || (changedBMI >= 0.6 && changedBMI < 1) || changedBMI >= 1) {
                                            textview_currentState.setText("Obesity So Bad");
                                        }
                                        break;
                                    case "Overweight":
                                        if ((changedBMI < -1) || (changedBMI >= 0.3 && changedBMI < 0.6)) {
                                            textview_currentState.setText("Overweight Be Careful");
                                        } else if ((changedBMI >= -1 && changedBMI < -0.6)) {
                                            textview_currentState.setText("Overweight Go Ahead");
                                        } else if ((changedBMI >= -0.6 && changedBMI < -0.3)) {
                                            textview_currentState.setText("Overweight Still Good");
                                        } else if ((changedBMI >= -0.3 && changedBMI < 0) || (changedBMI >= 0 && changedBMI < 0.3)) {
                                            textview_currentState.setText("Overweight Little Changes");
                                        } else if ((changedBMI >= 0.6 && changedBMI < 1) || changedBMI >= 1) {
                                            textview_currentState.setText("Overweight So Bad");
                                        }
                                        break;

                                    case "Healthy Weight":
                                        if ((changedBMI < -1)) {
                                            textview_currentState.setText("Healthy Weight So Bad");
                                        } else if ((changedBMI >= -1 && changedBMI < -0.6) || (changedBMI >= -0.6 && changedBMI < -0.3) ||
                                                (changedBMI >= 0.3 && changedBMI < 0.6) || (changedBMI >= 0.6 && changedBMI < 1) || changedBMI >= 1) {
                                            textview_currentState.setText("Healthy Weight Be Careful");
                                        } else if ((changedBMI >= -0.3 && changedBMI < 0) || (changedBMI >= 0 && changedBMI < 0.3)) {
                                            textview_currentState.setText("Healthy Weight Little Changes");
                                        }
                                        break;


                                    case "Underweight":
                                        if ((changedBMI < -1) || (changedBMI >= -1 && changedBMI < -0.6) || (changedBMI >= -0.6 && changedBMI < -0.3)) {
                                            textview_currentState.setText("Underweight So Bad");
                                        } else if ((changedBMI >= -0.3 && changedBMI < 0) || (changedBMI >= 0 && changedBMI < 0.3)) {
                                            textview_currentState.setText("Underweight Little Changes");
                                        } else if ((changedBMI >= 0.3 && changedBMI < 0.6)) {
                                            textview_currentState.setText("Underweight Still Good");
                                        } else if ((changedBMI >= 0.6 && changedBMI < 1) || changedBMI >= 1) {
                                            textview_currentState.setText("Underweight Go Ahead");
                                        }
                                        break;
                                }

                                recyclerViewRecordsAdapter = new RecyclerViewRecordsAdapter(getApplicationContext(), recordsList);
                                recyclerViewRecords.setHasFixedSize(true);
                                recyclerViewRecords.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                recyclerViewRecords.setAdapter(recyclerViewRecordsAdapter);
                                recyclerViewRecordsAdapter.notifyDataSetChanged();

                            }
                            recyclerViewRecordsAdapter = new RecyclerViewRecordsAdapter(getApplicationContext(), recordsList);
                            recyclerViewRecords.setHasFixedSize(true);
                            recyclerViewRecords.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerViewRecords.setAdapter(recyclerViewRecordsAdapter);
                            recyclerViewRecordsAdapter.notifyDataSetChanged();

                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void bmiCalc() {
        if (age >= 20) {
            newAge = 1;
            newBMI = (newWeight / newLength) * newAge;
        } else if ((age > 10 && age < 21) && gender.equals("Male")) {
            newAge = 90 / 100;
            newBMI = (newWeight / newLength) * newAge;
        } else if ((age > 10 && age < 21) && gender.equals("Female")) {
            newAge = 80 / 100;
            newBMI = (newWeight / newLength) * newAge;
        } else if (age > 1 && age < 11) {
            newAge = 70 / 100;
            newBMI = (newWeight / newLength) * newAge;
        }
    }
}