package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class New_Record extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private TextView tv_back;
    private Button button_saveDataRecord;
    private Button plus_weight, plus_length, minus_weigth, minus_length;
    private TextView date, time;
    private EditText weight;
    private EditText length;

    double  newAge, newBMI;
    String state, dob, gender;


    private String currentId;


    private FirebaseAuth firebaseAuth;

    //Connection to Firebase FireStore
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        weight = findViewById(R.id.edittext_weight_newRecord);
        length = findViewById(R.id.edittext_length_newRecord);
        date = findViewById(R.id.textview_date_newRecord);
        time = findViewById(R.id.textview_time_newRecord);
        plus_weight =findViewById(R.id.btn_plusWeight_newRecord);
        minus_weigth =findViewById(R.id.btn_minusWeight_newRecord);
        plus_length =findViewById(R.id.btn_plusLength_newRecord);
        minus_length =findViewById(R.id.btn_minusLength_newRecord);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentId = firebaseAuth.getUid();

        db.collection("Users").document(currentId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            dob = task.getResult().get("DOB").toString();
                            gender = task.getResult().get("gender").toString();
                        }
                    }
                });


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");

            }
        });

        minus_weigth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newWeight = Integer.parseInt(weight.getText().toString());
                newWeight--;
                weight.setText(String.valueOf(newWeight));
            }
        });

        plus_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newWeight = Integer.parseInt(weight.getText().toString());
                newWeight++;
                weight.setText(String.valueOf(newWeight));
            }
        });

        minus_length.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newLength = Integer.parseInt(length.getText().toString());
                newLength--;
                length.setText(String.valueOf(newLength));
            }
        });

        plus_length.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newLength = Integer.parseInt(length.getText().toString());
                newLength++;
                length.setText(String.valueOf(newLength));
            }
        });


        tv_back = findViewById(R.id.back_home_newRecord);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(New_Record.this, Home.class));
                finish();
            }
        });

        button_saveDataRecord = findViewById(R.id.button_saveDataRecord);
        button_saveDataRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(date.getText().toString())
                && !TextUtils.isEmpty(time.getText().toString())) {

                    Records newItem = new Records();
                    newItem.setUserId(currentId);
                    newItem.setDate(date.getText().toString());
                    newItem.setWeight(weight.getText().toString());
                    newItem.setLength(length.getText().toString());
                    newItem.setChosenTime(time.getText().toString());

                    Calendar calendar = Calendar.getInstance();

                    int year = Integer.parseInt(dob.substring(7));
                    int age = calendar.get(Calendar.YEAR) - year;
                    double newWeight = Double.parseDouble(weight.getText().toString());
                    double newLength = Math.pow(Double.parseDouble(length.getText().toString()) / 100, 2);

                    if (age >= 20) {
                        newAge = 1;
                        newBMI = (newWeight / newLength) * newAge;
                    } else if ((age > 10 && age < 21) && gender.equals("Male")) {
                        newAge = 90/100;
                        newBMI = (newWeight / newLength) * newAge;
                    } else if ((age > 10 && age < 21) && gender.equals("Female")) {
                        newAge = 80/100;
                        newBMI = (newWeight / newLength) * newAge;
                    } else if (age > 1 && age < 11) {
                        newAge = 70/100;
                        newBMI = (newWeight / newLength) * newAge;
                    }

                    if (newBMI > 30) {
                        state = "Obesity";
                    } else if (newBMI >= 25 && newBMI < 30) {
                        state = "Overweight";
                    } else if (newBMI >= 18.5 && newBMI < 25) {
                        state = "Healthy Weight";
                    } else if (newBMI < 18.5) {
                        state = "Underweight";
                    }

                    newItem.setState(state);
                    newItem.setBmi(String.valueOf(newBMI));

                    db.collection("Records").add(newItem)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                                        Intent intent =new Intent(getApplicationContext(), Home.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                } else {
                    Toast.makeText(getApplicationContext(), "Empty Filled Not Allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String newFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(newFormat, Locale.US);
        date.setText(simpleDateFormat.format(c.getTime()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        time.setText(i + ":" + i1);
    }
}