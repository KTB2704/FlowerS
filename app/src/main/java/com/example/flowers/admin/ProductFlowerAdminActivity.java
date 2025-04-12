package com.example.flowers.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowers.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.admin.ProductAdminAdapter;
import adapter.user.SearchAdapter;
import domain.Flowers;

public class ProductFlowerAdminActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView14;
    private TextView btnAdd;

    private List<Flowers> flowersList;

    private ProductAdminAdapter productAdminAdapter;

    Map<Flowers, String> flowerKeyMap = new HashMap<>(); // Map lưu trữ key (id) tương ứng với flowers từ Firebase

    private ImageView imgBack, btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_flower_admin);

        recyclerView14 = findViewById(R.id.recycleView14);
        btnAdd = findViewById(R.id.btnUpdate);
        imgBack = findViewById(R.id.imgBack);
        btnSetting = findViewById(R.id.btnSetting);

        // Setup RecyclerView cho danh sách flowers
        recyclerView14.setLayoutManager(new LinearLayoutManager(this));
        flowersList = new ArrayList<>();
        productAdminAdapter = new ProductAdminAdapter(this, flowersList, flowerKeyMap);
        recyclerView14.setAdapter(productAdminAdapter);

        // Bắt sự kiện khi nhấn vào btnAdd sẽ chuyển sang AddFlowerAdminActivity
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ProductFlowerAdminActivity.this, AddFlowerAdminActivity.class);
            startActivity(intent);
        });


        listFlowers(); // Load danh sách flower

        // Bắt sự kiện khi nhấn vào imgBack sẽ chuyển về màn hình trước
        imgBack.setOnClickListener(v ->
                {
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                });

        // Bắt sự kiện khi nhấn vào btnSetting sẽ chuyển sang ProfileAdminActivity
        btnSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileAdminActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

    }

    // Hàm đọc dữ liệu flowers  từ Firebase
    private void listFlowers() {
        databaseReference = FirebaseDatabase.getInstance().getReference("flowers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                flowersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Flowers flower = dataSnapshot.getValue(Flowers.class);
                    flowersList.add(flower);
                    flowerKeyMap.put(flower, dataSnapshot.getKey()); // Lưu key (ID) tương ứng
                }
                productAdminAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi khi tải danh sách hoa: " + error.getMessage());
            }
        });
    }
}