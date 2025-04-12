package com.example.flowers.user;

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

import adapter.user.CustomBottomNavigation;
import adapter.user.OrderAdapter;
import domain.Orders;

public class PendingUserActivity extends AppCompatActivity {
    private TextView btnDelivery, btnAccept, btnCancel;
    private ImageView btnBack;
    private RecyclerView recyclerView9;
    private OrderAdapter orderAdapter;
    private List<Orders> orderList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pending_user);

        // Cài đặt navigation bottom user
        BottomNavigationView bnv = findViewById(R.id.BottomNavView);
        bnv.setSelectedItemId(R.id.nav_users);
        CustomBottomNavigation.setupBottomNavigation(this, bnv);

        btnDelivery = findViewById(R.id.btnDelivery);
        btnAccept = findViewById(R.id.btnAccept);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);

        // Setup RecyclerView cho orders
        recyclerView9 = findViewById(R.id.RecycleView9);
        recyclerView9.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, orderList);
        recyclerView9.setAdapter(orderAdapter);
        recyclerView9.setNestedScrollingEnabled(false); // Tắt cuộn lồng nhau nếu trong ScrollView

        // Lấy userId từ SharedPreferences
        userId = getUserIdFromLocal();
        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Truy cập node orders/users/" + userId + "/pending  của người dùng trong Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("orders/users/" + userId + "/pending");

        switchActivity();  // Chuyển đổi intent giữa các trạng thái đơn hàng

        loadPendingOrders(); // Load đơn hàng đang chờ từ firebase

        // Bắt sự kiện khi nhấn vào btnBack sẽ chuyển về màn hình trước đó
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    // Lấy dữ liệu đơn hàng từ Firebase
    private void loadPendingOrders() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Orders order = orderSnapshot.getValue(Orders.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PendingActivity", "Lỗi tải đơn hàng: " + error.getMessage());
            }
        });
    }

    // Lấy ID người dùng từ bộ nhớ cục bộ (SharedPreferences)
    private String getUserIdFromLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

    // Cài đặt các nút điều hướng đến các loại trạng thái đơn hàng khác
    private void switchActivity() {
        btnDelivery.setOnClickListener(v -> {
            Intent intent = new Intent(PendingUserActivity.this, DeliveryUserActivity.class);
            overridePendingTransition(0, 0);
            startActivity(intent);
        });

        btnAccept.setOnClickListener(v -> {
            Intent intent = new Intent(PendingUserActivity.this, SuccessUserActivity.class);
            overridePendingTransition(0, 0);
            startActivity(intent);
        });
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(PendingUserActivity.this,CancelUserActivity.class);
            overridePendingTransition(0, 0);
            startActivity(intent);
        });
    }
}