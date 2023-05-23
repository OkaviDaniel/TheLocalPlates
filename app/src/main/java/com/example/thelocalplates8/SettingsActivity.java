package com.example.thelocalplates8;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thelocalplates8.Controllers.CustomerController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvFirstName, tvLastName, tvEmail;
    private ImageView profileImageView;

    private Uri imageUri;

    private CustomerController customerController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Firebase Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        tvFirstName = (TextView) findViewById(R.id.firstNameSettings);
        tvLastName = (TextView)findViewById(R.id.lastNameSettings);
        tvEmail = (TextView)findViewById(R.id.emailSettings);
        profileImageView = (ImageView) findViewById(R.id.profileImageViewSettings);

        customerController = new CustomerController();
        customerController.checkIfImageExist(currentUser.getUid(), new CustomerController.CheckProfileImageExist(){

            @Override
            public void onCheckProfileImageExist(Bitmap bitmap) {
                if(bitmap != null){

                }
            }
        });



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Log.d("Check1", "HELLOO" + user.firstName);
                    tvFirstName.setText("First Name: " + user.firstName);
                    tvLastName.setText("Last Name: " + user.lastName);
                    tvEmail.setText("Email: " + user.email);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        onActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> onActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        imageUri = data.getData();
                        profileImageView.setImageURI(imageUri);


                        customerController.uploadImage(imageUri, SettingsActivity.this, new CustomerController.UploadProfileImage() {
                            @Override
                            public void onUploadProfileImage(Boolean uploaded) {
                                Toast.makeText(SettingsActivity.this, "Profile Image uploaded!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

}