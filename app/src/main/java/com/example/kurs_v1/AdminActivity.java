package com.example.kurs_v1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getUsernameFromDatabase();
        Button button = findViewById(R.id.BackfromAdmin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    public void getUsernameFromDatabase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            databaseReference.child("username").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String username = task.getResult().getValue(String.class);
                        displayUsername(username);
                    } else {
                        Log.e("FirebaseError", "Ошибка получения имени пользователя: " + task.getException().getMessage());
                        displayDefaultMessage();
                    }
                } else {
                    Log.e("FirebaseError", "Ошибка получения имени пользователя: " + task.getException().getMessage());
                    displayDefaultMessage();
                }
            });
        }
        else{
            Log.e("FirebaseError", "Ошибка существования пользователя");
            displayDefaultMessage();
        }
    }

    private void displayUsername(String username) {
        TextView usernameTextView = findViewById(R.id.AdminHello);
        usernameTextView.setText(" Добро пожаловать, мой админ " + username + "!");
    }

    private void displayDefaultMessage() {
        TextView usernameTextView = findViewById(R.id.AdminHello);
        usernameTextView.setText("Добро пожаловать! мой админ");
    }

    public void signOut() {
        mAuth.signOut();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Toast.makeText(AdminActivity.this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}