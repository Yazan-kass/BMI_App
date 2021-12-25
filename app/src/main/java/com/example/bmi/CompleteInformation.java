package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CompleteInformation extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button button_saveDataInformation;
    private RadioGroup gender;
    private RadioButton sex;
    private EditText weight;
    private EditText length;
    private TextView dob;
    private Button plus_weight, plus_length, minus_weigth, minus_length;

    double newAge, newBMI;
    String state;

    private String currentId;

    private FirebaseAuth firebaseAuth;

    //Connection to Firebase FireStore
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_information);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentId = bundle.getString("userId");
        }

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentId = firebaseAuth.getUid();

        gender = findViewById(R.id.gender);
        sex = gender.findViewById(R.id.male);
        weight = findViewById(R.id.edittext_weight);
        length = findViewById(R.id.edittext_length);
        dob = findViewById(R.id.textview_dob);
        plus_weight = findViewById(R.id.btn_plusWeight);
        minus_weigth = findViewById(R.id.btn_minusWeight);
        plus_length = findViewById(R.id.btn_plusLength);
        minus_length = findViewById(R.id.btn_minusLength);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                sex = radioGroup.findViewById(i);
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

        button_saveDataInformation = findViewById(R.id.button_saveDataInformation);
        button_saveDataInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(weight.getText().toString())
                        && !TextUtils.isEmpty(length.getText().toString())
                        && !TextUtils.isEmpty(dob.getText().toString())) {
                    saveRecords(v);
                } else {
                    Snackbar.make(v, "Empty Field not Allowed!", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void saveRecords(View view) {

        if (TextUtils.isEmpty(dob.getText().toString())) {
            Toast.makeText(CompleteInformation.this, "Enter Your Birthday", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> newItem = new HashMap<>();
        newItem.put("gender", sex.getText().toString().trim());
        newItem.put("weight", weight.getText().toString().trim());
        newItem.put("length", length.getText().toString().trim());
        newItem.put("DOB", dob.getText().toString().trim());

        Calendar calendar = Calendar.getInstance();

        int year = Integer.parseInt(dob.getText().toString().substring(7));
        int age = calendar.get(Calendar.YEAR) - year;
        double newWeight = Double.parseDouble(weight.getText().toString());
        double newLength = Math.pow(Double.parseDouble(length.getText().toString()) / 100, 2);


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

        if (newBMI > 30) {
            state = "Obesity";
        } else if (newBMI >= 25 && newBMI < 30) {
            state = "Overweight";
        } else if (newBMI >= 18.5 && newBMI < 25) {
            state = "Healthy Weight";
        } else if (newBMI < 18.5) {
            state = "Underweight";
        }


        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        Records newRecords = new Records();
        newRecords.setDate(formattedDate);
        newRecords.setDob(dob.getText().toString());
        newRecords.setWeight(weight.getText().toString());
        newRecords.setLength(length.getText().toString());
        newRecords.setUserId(currentId);
        newRecords.setState(state);
        newRecords.setBmi(String.valueOf(newBMI));

        db.collection("Records").add(newRecords)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        db.collection("Users").document(currentId).update(newItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startActivity(new Intent(CompleteInformation.this, Home.class));
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
        dob.setText(simpleDateFormat.format(c.getTime()));
    }
}