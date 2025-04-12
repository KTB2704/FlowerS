package com.example.flowers.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowers.LoginActivity;
import com.example.flowers.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import adapter.user.CustomBottomNavigation;

public class ProfileUserActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private TextView txtName, btnLogOut, txtOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        // Cài đặt navigation bottom user
        BottomNavigationView bnv = findViewById(R.id.BottomNavView);
        bnv.setSelectedItemId(R.id.nav_users);
        CustomBottomNavigation.setupBottomNavigation(this, bnv);

        txtName = findViewById(R.id.txtName);
        btnLogOut = findViewById(R.id.btnLogOut);
        txtOrder = findViewById(R.id.txtOrder);

        // Lấy thông tin email từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInEmail = preferences.getString("email", "");

        if (loggedInEmail.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy email đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        // Truy cập tới node "users" trong Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Tìm user dựa trên email đã lưu và hiển thị tên
        databaseReference.orderByChild("email").equalTo(loggedInEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String name = userSnapshot.child("name").getValue(String.class);
                                txtName.setText(name);
                                return;
                            }
                        } else {
                            txtName.setText("Không tìm thấy user!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfileUserActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });

        // Xử lý khi người dùng nhấn nút Đăng xuất
        btnLogOut.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(ProfileUserActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileUserActivity.this, LoginActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        // Xử lý khi người dùng nhấn vào mục "Đơn hàng" chuyển sang màn hình PendingUserActivity
        txtOrder.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileUserActivity.this, PendingUserActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}
