package com.example.supnumarchive;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button profileBtn = findViewById(R.id.profile_btn);

        profileBtn.setOnClickListener(v -> {
            Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(profileIntent);

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}