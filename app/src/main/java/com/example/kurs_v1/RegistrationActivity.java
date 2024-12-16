package com.example.kurs_v1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    private EditText usernameInput, emailInput, passwordInput, confirmpassworInput;
    private Button registerButton, backButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        usernameInput = findViewById(R.id.usernameRegistration);
        emailInput = findViewById(R.id.emailRegistration);
        passwordInput = findViewById(R.id.passwordRegistration);
        confirmpassworInput = findViewById(R.id.confirmpasswordRegistration);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);

        registerButton.setOnClickListener(v -> registerUser());
        backButton.setOnClickListener(v -> BackinLoginUser());
    }

    private void registerUser() {
        String username = usernameInput.getText().toString();
        String emailText = emailInput.getText().toString();
        String passwordText = passwordInput.getText().toString();
        String passwordconfirmText = confirmpassworInput.getText().toString();

        if (username.isEmpty() || emailText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля перед регистрацией", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordconfirmText.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, Повторите пароль для завершения регистрации", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUsernameToDatabase(user.getUid(), username);

                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void BackinLoginUser() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void saveUsernameToDatabase(String userId, String username) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        User user = new User(username, "user");

        databaseReference.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegistrationActivity.this, "Имя пользователя сохранено", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegistrationActivity.this, "Ошибка сохранения имени пользователя", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
