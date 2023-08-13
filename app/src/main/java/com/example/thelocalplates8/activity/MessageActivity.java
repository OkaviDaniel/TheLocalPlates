package com.example.thelocalplates8.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.thelocalplates8.Controllers.MessageController;
import com.example.thelocalplates8.R;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        MessageController messageController = new MessageController(this);


    }
}