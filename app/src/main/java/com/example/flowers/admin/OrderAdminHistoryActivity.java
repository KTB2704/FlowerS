package com.example.flowers.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
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
import adapter.admin.OrderAdminHistoryAdapter;
import domain.Orders;

public class OrderAdminHistoryActivity extends AppCompatActivity {
    private TextView btnNew, btnAccept;
    private RecyclerView recyclerView13;
    private OrderAdminHistoryAdapter orderAdminHistoryAdapter;
    private List<Orders> orderList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private String userId;
    private ImageView btnSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_admin_history);

        // Cài đặt navigation bottom admin
        BottomNavigationView bnv = findViewById(R.id.BottomNavViewAdmin);
        bnv.setSelectedItemId(R.id.nav_order);
        CustomBottomNavigationAdmin.setupBottomNavigationAdmin(this, bnv);

        btnAccept = findViewById(R.id.btnAccept);
        btnNew = findViewById(R.id.btnNew);
        btnSetting = findViewById(R.id.btnSetting);

        // Setup RecyclerView cho orderAdmin
        recyclerView13 = findViewById(R.id.recycleView13);
        recyclerView13.setLayoutManager(new LinearLayoutManager(this));
        orderAdminHistoryAdapter = new OrderAdminHistoryAdapter(this, orderList);
        recyclerView13.setAdapter(orderAdminHistoryAdapter);

        // Lấy userId từ SharedPreferences
        userId = getUserIdFromLocal();
        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("orders/admin");

        switchActivity(); // Chuyển đổi intent giữa các trạng thái đơn hàng

        loadHistoryOrders(); // Load lịch sử đơn hàng từ firebase

        // Bắt sự kiện khi nhấn vào btnSetting sẽ chuyển sang ProfileAdminActivity
        btnSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileAdminActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    // Lấy dữ liệu đơn hàng từ Firebase
    private void loadHistoryOrders() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();

                for (DataSnapshot statusSnapshot : snapshot.getChildren()) {
                    if (!statusSnapshot.getKey().equals("pending")) {
                        for (DataSnapshot orderSnapshot : statusSnapshot.getChildren()) {
                            Orders order = orderSnapshot.getValue(Orders.class);
                            if (order != null) {
                                orderList.add(order);
                            }
                        }
                    }
                }
                orderAdminHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderAdminHistoryActivity", "Lỗi tải đơn hàng: " + error.getMessage());
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
        btnAccept.setOnClickListener(v -> {
            Intent intent = new Intent(OrderAdminHistoryActivity.this, OrderAdminAcceptActivity.class);
            overridePendingTransition(0, 0);
            startActivity(intent);
        });

        btnNew.setOnClickListener(v -> {
            Intent intent = new Intent(OrderAdminHistoryActivity.this, OrderAdminActivity.class);
            overridePendingTransition(0, 0);
            startActivity(intent);
        });
    }
}