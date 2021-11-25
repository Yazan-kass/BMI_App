package com.example.bmi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CompleteInformation extends AppCompatActivity {
    private Button button_saveDataInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_information);
        button_saveDataInformation = findViewById(R.id.button_saveDataInformation);

        button_saveDataInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CompleteInformation.this, home.class);
                startActivity(i);
                finish();
            }
        });
    }
}