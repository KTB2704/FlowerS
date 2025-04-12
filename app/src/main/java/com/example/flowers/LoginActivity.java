package com.example.flowers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowers.admin.AdminActivity;
import com.example.flowers.user.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLog, btnRe;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLog = findViewById(R.id.btnLog);
        btnRe = findViewById(R.id.btnRe);

        // Trỏ đến node "users" trong Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Bắt sự kiện khi bấm nút Đăng nhập
        btnLog.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            checkLogin(email, password);
        });

        // Bắt sự kiện khi bấm nút "Đăng ký"
        btnRe.setOnClickListener(v1 -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    // Hàm xử lí đăng nhập
    private void checkLogin(String email, String password) {
        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String userId = userSnapshot.getKey();
                                String storedPassword = userSnapshot.child("password").getValue(String.class);
                                String role = userSnapshot.child("role").getValue(String.class);

                                if (storedPassword != null && storedPassword.equals(password)) {
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                                    // Lưu thông tin người dùng vào SharedPreferences
                                    saveUserInfo(userId, email, role);

                                    Intent intent;
                                    if ("admin".equals(role)) {
                                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                                    } else {
                                        intent = new Intent(LoginActivity.this, MainActivity.class);
                                    }

                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Sai mật khẩu! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Lưu thông tin người dùng vào SharedPreferences để sử dụng sau này
    private void saveUserInfo(String userId, String email, String role) {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userId", userId);
        editor.putString("email", email);
        editor.putString("role", role);
        editor.apply();
    }
}
