package com.theandrodev.active;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    ImageButton imgbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgbutton = findViewById(R.id.Pushupsbutton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             openNewActivity();
            }
        });

    }
    public void openNewActivity(){
        Intent intent = new Intent(this, PushupsCounter.class);
        startActivity(intent);
    }
}