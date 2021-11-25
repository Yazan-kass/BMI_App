package com.example.bmi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Signup extends AppCompatActivity {

    private TextView tv_login;

    private Button button_creat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        tv_login = findViewById(R.id.textview_login);

        button_creat = findViewById(R.id.button_create);

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signup.this, Login.class);
                startActivity(i);
            }
        });

        button_creat.setOnClickListener(new View.OnClickListener() {
          @Override
            public void onClick(View v) {
                Intent i = new Intent(Signup.this, CompleteInformation.class);
                startActivity(i);
            }
        });
    }
}