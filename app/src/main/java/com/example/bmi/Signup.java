package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private TextView tv_login;
    private EditText editText_userName;
    private EditText editText_password;
    private EditText editText_email;
    private EditText edittext_rePassword;
    private Button btn_create;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;


    //FireStore Database connection
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        tv_login = findViewById(R.id.textview_login);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signup.this, Login.class);
                startActivity(i);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editText_userName = findViewById(R.id.edittext_name);
        editText_password = findViewById(R.id.edittext_password);
        editText_email = findViewById(R.id.edittext_email);
        edittext_rePassword =findViewById(R.id.edittext_Re_password);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    //user is already loggedin..
                } else {
                    //no user yet...
                }
            }
        };


        btn_create = findViewById(R.id.button_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText_email.getText().toString())
                        && !TextUtils.isEmpty(editText_password.getText().toString())
                        && !TextUtils.isEmpty(editText_userName.getText().toString())
                        && !TextUtils.isEmpty(edittext_rePassword.getText().toString())) {

                    String email = editText_email.getText().toString().trim();
                    String password = editText_password.getText().toString().trim();
                    String username = editText_userName.getText().toString().trim();
                    String repass = edittext_rePassword.getText().toString().trim();

                    if (password.equals(repass)) {
                        createUserEmailAccount(username, email, password, repass);
                    }else {
                        Toast.makeText(Signup.this,
                                "Password Doesn't Match!",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(Signup.this,
                            "Empty Fields Not Allowed!",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private void createUserEmailAccount(String username, String email, String password, String repass) {
        if (!TextUtils.isEmpty(username)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(repass)) {

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                DocumentReference documentReference = db.collection("Users")
                                        .document(firebaseAuth.getUid());
                                //we take user to ComplateInformationActivity
                                currentUser = firebaseAuth.getCurrentUser();
                                String currentUserId = currentUser.getUid();

                                //Create a user Map so we can create a user in the User collection
                                Map<String, String> userObj = new HashMap<>();
                                userObj.put("userId", currentUserId);
                                userObj.put("username", username);

                                //save to our firebase database
                                documentReference.set(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                String name = username;

                                                Intent intent = new Intent(Signup.this, CompleteInformation.class);
                                                intent.putExtra("userId", currentUserId);
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });

                            } else {
                                //something went wrong
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        } else {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}