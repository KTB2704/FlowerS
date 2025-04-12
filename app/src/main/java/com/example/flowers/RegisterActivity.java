package com.example.flowers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import domain.Users;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout TextInputLayoutEmail, TextInputLayoutName, TextInputLayoutPassword, TextInputLayoutCfPass;

    private TextInputEditText etEmail, etName, etPassword, etCfPass;
    private Button btnRes;
    private ImageView btnBack;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        TextInputLayoutEmail = findViewById(R.id.TextInputLayoutEmail);
        TextInputLayoutName = findViewById(R.id.TextInputLayoutName);
        TextInputLayoutPassword = findViewById(R.id.TextInputLayoutPassword);
        TextInputLayoutCfPass = findViewById(R.id.TextInputLayoutCfPass);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPass);
        etCfPass = findViewById(R.id.etCfPass);
        btnBack = findViewById(R.id.btnBack);
        btnRes = findViewById(R.id.btnRes);

        // Trỏ đến node "users" trong Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Quay lại màn hình trước đó khi bấm nút "Back"
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Bắt sự kiện khi bấm nút "Đăng ký"
        btnRes.setOnClickListener(v -> registerUser());

    }

    // Hàm xử lí đăng kí
    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etCfPass.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }
        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng xác nhận mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(RegisterActivity.this, "Email đã tồn tại! Vui lòng chọn email khác.", Toast.LENGTH_SHORT).show();
                        } else {
                            String userId = databaseReference.push().getKey();

                            Users newUser = new Users(email, name, password, "user", userId);

                            databaseReference.child(userId).setValue(newUser)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RegisterActivity.this, "Lỗi kết nối! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}