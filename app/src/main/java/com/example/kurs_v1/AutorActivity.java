package com.example.kurs_v1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AutorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autor);
        ImageView imageView = findViewById(R.id.imageView);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Button b = findViewById(R.id.Anime);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.startAnimation(fadeIn);
            }
        });


        Button button = findViewById(R.id.BackstoMenu);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AutorActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}