package com.example.bmi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddFood extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText foodName;
    private EditText calories;
    private Spinner foodType;
    private ImageView food_img;
    private Button btn_upload;
    private Button btn_save;
    private TextView back_home_addFood;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    private Food newItem;

    private Uri img_URI, uri;
    private String currentId, category;
    private ProgressDialog progressDialog;

    private int count = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        foodName = findViewById(R.id.edittext_foodName);
        calories = findViewById(R.id.edittext_calorie);
        foodType = findViewById(R.id.spinner_foodType);
        back_home_addFood = findViewById(R.id.back_home_addFood);
        back_home_addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        food_img = findViewById(R.id.imageView_Food_addFood);
        btn_save = findViewById(R.id.button_Save_addFood);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentId = firebaseAuth.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        btn_upload = findViewById(R.id.button_Upload_Photo_addFood);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 20);
            }
        });

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.food_category, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodType.setAdapter(arrayAdapter);
        foodType.setOnItemSelectedListener(this);
        category = foodType.getSelectedItem().toString();

        newItem = new Food();
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(foodName.getText().toString())
                        && !TextUtils.isEmpty(calories.getText().toString())
                        && !(img_URI == null)) {


                    newItem = new Food();
                    newItem.setFoodName(foodName.getText().toString());
                    newItem.setCalorie(calories.getText().toString());
                    newItem.setCategory(category);
                    newItem.setSelectedIdItem(count);
                    newItem.setImg_URI(String.valueOf(uri));
                    newItem.setUserId(currentId);

                    db.collection("Food").add(newItem)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        calories.setText("");
                                        foodType.setSelection(0);
                                        foodName.setText("");
                                        Glide.with(getApplicationContext()).load(R.drawable.food).into(food_img);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20) {
            if (resultCode == Activity.RESULT_OK) {
                img_URI = data.getData();
                Glide.with(getApplicationContext()).load(img_URI).into(food_img);
                upload(img_URI);
            }
        }
    }

    private void upload(Uri image) {
        progressDialog = ProgressDialog.show(this, "Loading", "please, wait a moment", true);
        final StorageReference reference = this.storageReference.child(currentId);
        reference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        AddFood.this.uri = uri;
                        Toast.makeText(AddFood.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category = adapterView.getItemAtPosition(i).toString();
        count = adapterView.getSelectedItemPosition();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}