package com.example.kurs_v1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ArrayList<Product> products = new ArrayList<>();
    BoxAdapter boxAdapter;
    static TextView footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleteAllProductsForUser();
        setContentView(R.layout.activity_main);

        fillData();

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragmentloginout firstFragment = new Fragmentloginout();
        fragmentTransaction.add(R.id.fragmentloginout_container, firstFragment);

        FragmentList secondFragment = new FragmentList();
        fragmentTransaction.add(R.id.fragmentlist_container, secondFragment);

        fragmentTransaction.commit();
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
        TextView usernameTextView = findViewById(R.id.HelloText);
        usernameTextView.setText("Добро пожаловать, " + username + "!");
    }

    private void displayDefaultMessage() {
        TextView usernameTextView = findViewById(R.id.HelloText);
        usernameTextView.setText("Добро пожаловать!");
    }

    public void ShowPopupmenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_item1) {
                    Intent Inter = new Intent(MainActivity.this, AutorActivity.class);
                    startActivity(Inter);
                    return true;
                } else if (itemId == R.id.menu_item2) {
                    Intent i = new Intent(MainActivity.this, ApplicationActivity.class);
                    startActivity(i);
                    return true;
                } else if (itemId == R.id.menu_item3) {
                    signOut();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }


    public void signOut() {
        mAuth.signOut();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        Toast.makeText(MainActivity.this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    void fillData() {
        String[] products = {
                "Микрофон", "Синтезатор", "Электрогитара",
                "Бас", "Бас-бочка",
                "Звуковая карта", "Акустическая гитара",
                "Малый барабан", "Конденсаторный микрофон"
        };

        int[] prices = {200, 200, 200, 200, 150, 400, 200, 150, 600};

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");

        for (int i = 0; i < products.length; i++) {
            int imageId = getResources().getIdentifier("product" + (i + 1), "drawable", getPackageName());

            Product product = new Product(i + 1, products[i], prices[i], imageId, false);
            databaseReference.child(String.valueOf(product.id)).setValue(product);
        }
    }

    public void showChecked() {
        Intent i = new Intent(this, ShopActivity.class);
        startActivity(i);
    }

    private void deleteAllProductsForUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("users").child(user.getUid()).child("products");

            // Удаляем все продукты
            databaseReference.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("FirebaseSuccess", "Все продукты успешно удалены");
                } else {
                    Log.e("FirebaseError", "Ошибка при удалении продуктов: " + task.getException().getMessage());
                }
            });
        } else {
            Log.e("FirebaseError", "Пользователь не найден");
        }
    }
}


