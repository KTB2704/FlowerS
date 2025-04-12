package com.example.flowers.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.flowers.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.user.CategoryAdapter;
import adapter.user.CustomBottomNavigation;
import adapter.user.FlowerListAdapter;
import domain.Category;
import domain.Flowers;

public class WidgetUserActivity extends AppCompatActivity {
    private RecyclerView recyclerView3, recyclerView4;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar3, progressBar4;
    private List<Flowers> flowersList;

    private FlowerListAdapter FlowerListAdapter;

    private String selectedCategoryId;

    private EditText btnS;

    private ImageView imgCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_widget_user);

        // Cài đặt navigation bottom user
        BottomNavigationView bnv = findViewById(R.id.BottomNavView);
        bnv.setSelectedItemId(R.id.nav_widget);
        CustomBottomNavigation.setupBottomNavigation(this, bnv);

        EditText btnS = findViewById(R.id.btnS);
        imgCart = findViewById(R.id.imgCart);

        // Setup RecyclerView cho category
        int pinkcolor = ContextCompat.getColor(this, R.color.pink);
        recyclerView3 = findViewById(R.id.recyclerView3);
        progressBar3 = findViewById(R.id.progressBar3);
        recyclerView3.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList, this::onCategoryClicked, pinkcolor);
        recyclerView3.setAdapter(categoryAdapter);

        // Lấy categoryId được truyền từ MainActivity
        selectedCategoryId = getIntent().getStringExtra("categoryId");

        // Setup RecyclerView cho list flowers
        recyclerView4 = findViewById(R.id.recyclerView4);
        progressBar4 = findViewById(R.id.progressBar4);
        recyclerView4.setLayoutManager(new GridLayoutManager(this,2));
        flowersList = new ArrayList<>();
        FlowerListAdapter = new FlowerListAdapter(this, flowersList);
        recyclerView4.setAdapter(FlowerListAdapter);

        // Bắt sự kiện khi nhấn vào thanh search sẽ chuyển sang SearchUserActivity
        btnS.setOnClickListener(v -> {
            Intent intent = new Intent(WidgetUserActivity.this, SearchUserActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
        });

        initCate1(); // Load category từ Firebase

        // Nếu có category được chọn từ MainActivity thì lọc flowers theo category, ngược lại hiển thị toàn bộ hoa
        if (selectedCategoryId != null) {
            loadFlowersByCategory(selectedCategoryId);
        } else {
            listFlowers();
        }

        // Bắt sự kiện khi nhấn vào giỏ hàng sẽ chuyển sang CartActivity
        imgCart.setOnClickListener(v -> {
            Intent intent = new Intent(WidgetUserActivity.this, CartActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
        });


    }

    // Lấy danh sách flowers từ firebase
    private void listFlowers() {
        databaseReference = FirebaseDatabase.getInstance().getReference("flowers");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                flowersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Flowers flower = dataSnapshot.getValue(Flowers.class);
                    flowersList.add(flower);
                }
                FlowerListAdapter.notifyDataSetChanged();
                progressBar4.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi khi tải danh sách hoa: " + error.getMessage());
            }
        });
    }

    // Lấy danh sách flowers dựa theo ID category từ firebase
    private void loadFlowersByCategory(String categoryId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("flowers");
        databaseReference.orderByChild("categoryId").equalTo(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                flowersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Flowers flower = dataSnapshot.getValue(Flowers.class);
                    flowersList.add(flower);
                }
                FlowerListAdapter.notifyDataSetChanged();
                progressBar4.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi khi tải danh sách hoa: " + error.getMessage());
            }
        });
    }


    // Lấy danh sách các category từ firebase
    private void initCate1() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    categoryList.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
                progressBar3.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi khi tải dữ liệu", error.toException());
            }
        });
    }

    // Xử lý khi người dùng click vào 1 category
    private void onCategoryClicked(Category category, int position) {
        selectedCategoryId = category.getId(); // Cập nhật ID danh mục được chọn
        loadFlowersByCategory(selectedCategoryId); // Tải lại flower theo category mới
    }

}