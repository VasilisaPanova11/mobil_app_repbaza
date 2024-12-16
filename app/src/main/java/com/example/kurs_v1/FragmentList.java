package com.example.kurs_v1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.kurs_v1.databinding.ItemBinding;

import java.util.ArrayList;

public class FragmentList extends Fragment {
    ArrayList<Product> products = new ArrayList<>();
    static TextView footer;
    ListView lvMain;
    BoxAdapter boxAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmentlist, container, false);

        fetchProductsFromDatabase();

        boxAdapter = new BoxAdapter(getContext(), products);

        lvMain = view.findViewById(R.id.lvMain);
        lvMain.setAdapter(boxAdapter);

        footer = view.findViewById(R.id.tvFooter);
        footer.setText("Позиции: 0");


        Button logoutButton = view.findViewById(R.id.toArend);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).showChecked();
            }
        });

        return view;
    }

    public void fetchProductsFromDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                products.clear(); // Очищаем список перед добавлением новых данных
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        products.add(product); // Добавляем продукт в список
                    }
                }
                boxAdapter.notifyDataSetChanged(); // Уведомляем адаптер об изменениях
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Ошибка получения данных: " + databaseError.getMessage());
            }
        });
    }


    public static void updateFooter(int total, int totalPrice) {

        if (total == 0)
            footer.setText("Позиции: 0");
        else
            footer.setText("Позиции: " + total + "\n(Цена за все: " + totalPrice + ")");
    }

}

