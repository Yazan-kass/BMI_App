package com.example.bmi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditFoodActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText foodName;
    private EditText calories;
    private Spinner foodType;
    private ImageView food_img;
    private Button btn_upload;
    private Button btn_save;
    private TextView back;

    private Uri uri;
    ProgressDialog progressDialog;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    private String currentId;
    private String preImg_URI;
    private String preCategory;
    private int preSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        foodName = findViewById(R.id.edittext_foodName_editFood);
        calories = findViewById(R.id.edittext_calorie_editFood);
        foodType = findViewById(R.id.spinner_foodType_editFood);
        food_img = findViewById(R.id.imageView_food_editFood);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentId = firebaseAuth.getUid();

        Intent intent = getIntent();
        String preFoodName = intent.getStringExtra("foodName");
        String preCalorie = intent.getStringExtra("calorie");
        preCategory = intent.getStringExtra("category");
        String preFoodId = intent.getStringExtra("foodId");
        preSelectedItem = intent.getIntExtra("SelectedIdItem",0);
        preImg_URI = intent.getStringExtra("img_URI");

        foodName.setText(preFoodName);
        calories.setText(preCalorie);
        Glide.with(getApplicationContext()).load(preImg_URI).into(food_img);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.food_category, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodType.setAdapter(arrayAdapter);
        foodType.setSelection(preSelectedItem);
        foodType.setOnItemSelectedListener(this);


        storageReference = FirebaseStorage.getInstance().getReference();

        btn_upload = findViewById(R.id.button_Upload_Photo_editFood);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent1, 20);
            }
        });


        btn_save = findViewById(R.id.button_Save_editFood);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(foodName.getText().toString())
                && !TextUtils.isEmpty(calories.getText().toString())){

                    Map<String, Object> newItem = new HashMap<>();
                    String newFoodName = foodName.getText().toString();
                    String newCalorie = calories.getText().toString();


                    newItem.put("foodName", newFoodName);
                    newItem.put("img_URI", preImg_URI);
                    newItem.put("calorie", newCalorie);
                    newItem.put("foodType", preCategory);
                    db.collection("Food").document(preFoodId)
                            .update(newItem).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(getApplicationContext() , FoodListActivity.class));
                        }
                    });

                } else {
                    Toast.makeText(EditFoodActivity.this, "Empty Filled Not Allowed", Toast.LENGTH_SHORT).show();
                }


            }
        });


        back = findViewById(R.id.back_home_editFood);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditFoodActivity.this, FoodListActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20) {
            if (resultCode == Activity.RESULT_OK) {
                uri = data.getData();
                uplode(uri);
            }
        }
    }

    private void uplode(Uri uri1) {
        progressDialog = ProgressDialog.show(this, "Loading", "please, wait a moment", true);
        final StorageReference reference = storageReference.child(currentId);
        reference.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into(food_img);
                        preImg_URI = uri.toString();
                        progressDialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        preCategory = adapterView.getItemAtPosition(i).toString();
        preSelectedItem = adapterView.getSelectedItemPosition();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}