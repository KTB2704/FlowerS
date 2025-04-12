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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowers.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.user.BestFlowersAdapter;
import adapter.user.CategoryAdapter;
import adapter.user.CustomBottomNavigation;
import domain.Category;
import domain.Flowers;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView1, recyclerView2;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar1, progressBar2;
    private BestFlowersAdapter bestFlowersAdapter;
    private List<Flowers> flowersList;

    private EditText btnS;
    private ImageView imgCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Cài đặt navigation bottom user
        BottomNavigationView bnv = findViewById(R.id.BottomNavView);
        bnv.setSelectedItemId(R.id.nav_home);
        CustomBottomNavigation.setupBottomNavigation(this, bnv);

        btnS = findViewById(R.id.btnS);
        imgCart = findViewById(R.id.imgCart);

        // Setup RecyclerView cho adapter category
        recyclerView1 = findViewById(R.id.recyclerView1);
        progressBar1 = findViewById(R.id.progressBar1);
        recyclerView1.setLayoutManager(new GridLayoutManager(this,4));
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList, null, null);
        recyclerView1.setAdapter(categoryAdapter);

        // Setup RecyclerView cho adapter bestFlowers
        recyclerView2 = findViewById(R.id.recyclerView2);
        progressBar2 = findViewById(R.id.progressBar2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        flowersList = new ArrayList<>();
        bestFlowersAdapter = new BestFlowersAdapter(this, flowersList);
        recyclerView2.setAdapter(bestFlowersAdapter);

        // Bắt sự kiện khi nhấn vào thanh search sẽ chuyển sang SearchUserActivity
        btnS.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchUserActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
        });

        // Bắt sự kiện khi nhấn vào giỏ hàng sẽ chuyển sang CartActivity
        imgCart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
        });

        initCate();  // Load category từ Firebase
        initBestF(); // Load bestFlower từ Firebase
        setupCategoryClickListener();  // Gán sự kiện click vào category

    }

    /**
     * Gán sự kiện khi click vào item trong danh sách danh mục.
     * Mỗi khi chọn category → chuyển sang WidgetUserActivity và truyền ID category đó.
     */
    private void setupCategoryClickListener() {
        categoryAdapter.setOnItemClickListener((category, position) -> {
            Intent intent = new Intent(MainActivity.this, WidgetUserActivity.class);
            intent.putExtra("categoryId", category.getId());
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
        });
    }

    // Lấy danh sách các hoa nổi bật (bestFlower = true) từ Firebase
    private void initBestF() {
        databaseReference = FirebaseDatabase.getInstance().getReference("flowers");
        Query query = databaseReference.orderByChild("bestFlower").equalTo(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                flowersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Flowers flowers = dataSnapshot.getValue(Flowers.class);
                    flowersList.add(flowers);
                }
                bestFlowersAdapter.notifyDataSetChanged();
                progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi khi tải dữ liệu", error.toException());
            }
        });

    }

    // Lấy danh sách các category từ firebase
    private void initCate() {
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
                progressBar1.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi khi tải dữ liệu", error.toException());
            }
        });
    }

}