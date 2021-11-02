package com.example.bmi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    private TextView tv_next;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tv_next = findViewById(R.id.textview_next);



        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Splash.this, Login.class);
                startActivity(i);
                mHandler.removeCallbacksAndMessages(null);
            }
        });

       mHandler.postDelayed(new Runnable() {
           public void run() {

               activityChange();
           }
        }, 5000);

    }

   private void activityChange() {
        Intent intent = new Intent(Splash.this, Login.class);

        startActivity(intent);
   }


}