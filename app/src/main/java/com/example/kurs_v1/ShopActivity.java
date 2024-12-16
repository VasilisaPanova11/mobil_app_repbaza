package com.example.kurs_v1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    ArrayList<Product> products = new ArrayList<>();
    BoxAdapter boxAdapter;
    ListView toView;
    private static final String CHANNEL_ID = "channel_notification_arend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        createNotificationChannel();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users").child(user.getUid()).child("products");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total_selected = snapshot.child("total").getValue(int.class);
                int total_price = snapshot.child("totalPrice").getValue(int.class);
                TextView footer = findViewById(R.id.toFooter);
                if (total_selected == 0)
                    footer.setText("Позиций: 0");
                else
                    footer.setText("Позиций: " + total_selected + "\n(Цена за все: " + total_price + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        boxAdapter = new BoxAdapter(this, products);
        fetchUserAndProducts();

        ListView toMain = findViewById(R.id.toMain);
        toMain.setAdapter(boxAdapter);
    }


    public void fetchUserAndProducts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users").child(user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    ArrayList<String> productIds = new ArrayList<>();

                    // Получаем список ID продуктов
                    for (DataSnapshot productIdSnapshot : userSnapshot.child("products").getChildren()) {
                        boolean chbox = Boolean.TRUE.equals(userSnapshot.child("products").child(productIdSnapshot.getKey()).child("box").getValue(boolean.class));
                        if (chbox)
                            productIds.add(productIdSnapshot.getKey());

                    }

                    fetchProducts(productIds);
                } else {
                    Log.e("FirebaseError", "Пользователь не найден");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Ошибка получения данных пользователя: " + databaseError.getMessage());
            }
        });
    }

    private void fetchProducts(ArrayList<String> productIds) {
        products.clear();
        for (String productId : productIds) {
            DatabaseReference productRef = FirebaseDatabase.getInstance()
                    .getReference("products").child(productId);

            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                    if (productSnapshot.exists()) {
                        Product product = productSnapshot.getValue(Product.class);
                        products.add(product);
                        boxAdapter.notifyDataSetChanged();

                    } else {
                        Log.e("FirebaseError", "Продукт не найден");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("FirebaseError", "Ошибка получения данных продукта: " + databaseError.getMessage());
                }
            });
        }

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void NotificationSend(View view) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users").child(user.getUid()).child("products");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total_selected = snapshot.child("total").getValue(int.class);
                int total_price = snapshot.child("totalPrice").getValue(int.class);
                Log.d("Not", "NotificationSend: ");
                NotificationtoSend(view, total_selected, total_price);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void NotificationtoSend(View view, int total, int total_price) {
        Log.d("Not", "NotificationSend: ");

        if (total <= 0 || total_price <= 0) {
            Log.e("NotificationError", "Некорректные значения для уведомления");
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_image)
                .setContentTitle("Уведомление о аренде")
                .setContentText("Вы арендовали " + total + " инструментов ценой " + total_price + " рублей")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build()); // Отображаем уведомление с уникальным ID
    }
    public void onBackToMainClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

