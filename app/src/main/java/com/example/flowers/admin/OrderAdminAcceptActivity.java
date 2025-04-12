package com.example.flowers.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowers.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.admin.CustomBottomNavigationAdmin;
import adapter.admin.OrderAdminAdapter;
import domain.Orders;

public class OrderAdminAcceptActivity extends AppCompatActivity {
    private TextView btnNew, btnHistory;
    private RecyclerView recyclerView12;
    private OrderAdminAdapter orderAdminAdapter;
    private List<Orders> orderList = new ArrayList<>();
    private DatabaseReference databaseRef;
    private String userId;

    private ImageView btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_admin_accept);

        // Cài đặt navigation bottom admin
        BottomNavigationView bnv = findViewById(R.id.BottomNavViewAdmin);
        bnv.setSelectedItemId(R.id.nav_order);
        CustomBottomNavigationAdmin.setupBottomNavigationAdmin(this, bnv);

        btnNew = findViewById(R.id.btnNew);
        btnHistory = findViewById(R.id.btnHistory);
        btnSetting = findViewById(R.id.btnSetting);

        // Setup RecyclerView cho orderAdmin
        recyclerView12 = findViewById(R.id.recycleView14);
        recyclerView12.setLayoutManager(new LinearLayoutManager(this));
        orderAdminAdapter = new OrderAdminAdapter(this, orderList);
        recyclerView12.setAdapter(orderAdminAdapter);
        recyclerView12.setNestedScrollingEnabled(false);

        // Lấy userId từ SharedPreferences
        userId = getUserIdFromLocal();
        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseRef = FirebaseDatabase.getInstance().getReference("orders/admin/accepted");

        switchActivity(); // Chuyển đổi intent giữa các trạng thái đơn hàng

        loadAcceptOrder(); // Load đơn hàng đã xác nhận từ firebase

        // Bắt sự kiện khi nhấn vào btnSetting sẽ chuyển sang ProfileAdminActivity
        btnSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileAdminActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    // Lấy dữ liệu đơn hàng từ Firebase
    private void loadAcceptOrder() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Orders order = orderSnapshot.getValue(Orders.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                orderAdminAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderAdminActivity", "Lỗi tải đơn hàng: " + error.getMessage());
            }
        });
    }

    // Lấy ID người dùng từ bộ nhớ cục bộ (SharedPreferences)
    private String getUserIdFromLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

    // Cài đặt các nút điều hướng đến các loại trạng thái khác
    private void switchActivity() {
        btnNew.setOnClickListener(v -> {
            Intent intent = new Intent(OrderAdminAcceptActivity.this, OrderAdminActivity.class);
            overridePendingTransition(0, 0);
            startActivity(intent);
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(OrderAdminAcceptActivity.this, OrderAdminHistoryActivity.class);
            overridePendingTransition(0, 0);
            startActivity(intent);
        });
    }
}