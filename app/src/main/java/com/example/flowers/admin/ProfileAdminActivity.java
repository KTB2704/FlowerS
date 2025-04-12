package com.example.flowers.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flowers.LoginActivity;
import com.example.flowers.R;
import com.example.flowers.user.ProfileUserActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileAdminActivity extends AppCompatActivity {
    private ImageView btnBack;
    private TextView btnLogOut, txtName;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_admin);

        btnBack = findViewById(R.id.btnBack);
        btnLogOut = findViewById(R.id.btnLogOut);
        txtName = findViewById(R.id.txtName);

        // Khi nhấn nút quay lại thì đóng activity
        btnBack.setOnClickListener(v ->
        {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Lấy email từ SharedPreferences để truy xuất thông tin người dùng từ Firebase
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String loggedInEmail = preferences.getString("email", "");

        if (loggedInEmail.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy email đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        // Khởi tạo tham chiếu tới bảng "users" trong Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Truy vấn thông tin user theo email đã lưu
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
                        Toast.makeText(ProfileAdminActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });

        // Xử lý khi người dùng nhấn nút "Đăng xuất"
        btnLogOut.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(ProfileAdminActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileAdminActivity.this, LoginActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }
}