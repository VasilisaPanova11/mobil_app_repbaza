package com.example.kurs_v1;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater ltInflater;
    ArrayList<Product> objects;

    BoxAdapter(Context context, ArrayList<Product> products) {
        ctx = context;
        objects = products;
        ltInflater = (LayoutInflater)
                ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = ltInflater.inflate(R.layout.item, parent, false);

        Product p = getProduct(position);

        ((TextView) view.findViewById(R.id.tvId)).setText("Инстурсмент: " + p.id);
        ((TextView) view.findViewById(R.id.tvDescr)).setText(p.name);
        ((TextView) view.findViewById(R.id.tvPrice)).setText("Цена: " + p.price + " p/д");
        ((ImageView)
                view.findViewById(R.id.ivImage)).setImageResource(p.image);

        CheckBox cbBuy = (CheckBox) view.findViewById(R.id.chBox);
        cbBuy.setTag(position);
        cbBuy.setOnCheckedChangeListener(null);
        cbBuy.setChecked(p.box); // Устанавливаем состояние CheckBox из объекта Product

        // Устанавливаем слушатель изменений состояния CheckBox
        cbBuy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            p.box = isChecked; // Обновляем локальное состояние

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                        .getReference("users").child(user.getUid()).child("products").child(String.valueOf(p.id));
                databaseReference.child("box").setValue(isChecked)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("FirebaseSuccess", "Состояние CheckBox успешно сохранено");
                            } else {
                                Log.e("FirebaseError", "Ошибка сохранения состояния CheckBox: " + task.getException().getMessage());
                            }
                        });
            }

            // Обновляем общий счетчик (если необходимо)
            int total = 0, totalPrice = 0;
            for (Product product : objects) {
                if (product.box) {
                    total++;
                    totalPrice += product.price;
                }
            }
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("users").child(user.getUid()).child("products");
            databaseReference.child("total").setValue(total);
            databaseReference.child("totalPrice").setValue(totalPrice);
            FragmentList.updateFooter(total, totalPrice);
        });
        return view;
    }

    Product getProduct(int position) {
        return ((Product) getItem(position));
    }

    ArrayList<Product> getBox() {
        ArrayList<Product> box = new ArrayList<>();
        for (Product p : objects) {
            if (p.box)
                box.add(p);
        }
        return box;
    }


}
