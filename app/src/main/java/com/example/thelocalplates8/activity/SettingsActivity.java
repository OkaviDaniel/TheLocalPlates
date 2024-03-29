package com.example.thelocalplates8.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thelocalplates8.Controllers.CustomerController;
import com.example.thelocalplates8.R;
import com.example.thelocalplates8.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvFirstName, tvLastName, tvEmail;
    private ImageView profileImageView;

    private Button purchaseHistoryBtn;

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
        profileImageView.setVisibility(View.GONE);
        purchaseHistoryBtn = (Button)findViewById(R.id.buttonPH);

        customerController = new CustomerController();
        customerController.checkIfImageExist(currentUser.getUid(), new CustomerController.CheckProfileImageExist(){

            @Override
            public void onCheckProfileImageExist(String url) {
                if(url.length() > 0){
                    Picasso.get().load(url).into(profileImageView);
                    profileImageView.setVisibility(View.VISIBLE);
                }else{
                    profileImageView.setVisibility(View.VISIBLE);
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

                    SharedPreferences sharedPreferences = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userFirstName", user.firstName);
                    editor.putString("userLastName", user.lastName);
                    editor.apply();
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


        purchaseHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(SettingsActivity.this,"Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsActivity.this, OrderHistoryActivity.class);

                SharedPreferences sharedPreferences = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId", "");
                intent.putExtra("USERID", userId);
                startActivity(intent);
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