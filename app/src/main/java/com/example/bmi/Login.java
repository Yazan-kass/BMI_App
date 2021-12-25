package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private TextView tv_signUp;
    private Button btn_login;

    private EditText email;
    private EditText password;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv_signUp = findViewById(R.id.textview_signUp);


        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Signup.class);
                startActivity(i);
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);

        btn_login = findViewById(R.id.button_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginEmailPasswordUser(email.getText().toString().trim(),
                        password.getText().toString().trim());


            }
        });
    }

    private void loginEmailPasswordUser(String email, String pwd) {
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(pwd)) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                //Go to Home
                                startActivity(new Intent(Login.this,
                                        Home.class));

                            }else {
                                Toast.makeText(Login.this, "Email or password uncorrected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("LoginActivity", "onFailure: error");

                        }
                    });

        } else {
            Toast.makeText(Login.this, "Please Enter email and password",
                    Toast.LENGTH_SHORT).show();
        }
    }
}