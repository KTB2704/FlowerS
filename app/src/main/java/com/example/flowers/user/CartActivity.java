package com.example.flowers.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowers.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import adapter.user.CartItemAdapter;
import domain.CartItem;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView7;
    private CartItemAdapter cartAdapter;
    private List<CartItem> cartList;
    private DatabaseReference databaseReference;
    private String userId;
    private ImageView imgBack;
    private TextView priceTotal;
    private Button btnPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        imgBack = findViewById(R.id.imgBack);
        priceTotal = findViewById(R.id.priceTotal);
        btnPayment = findViewById(R.id.btnPayment);

        // Lấy userId từ SharedPreferences
        userId = getUserIdFromLocal();
        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tham chiếu tới node Cart/userId trong Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId);

        // Setup RecyclerView cho sản phẩm trong giỏ hàng
        recyclerView7 = findViewById(R.id.recycleView7);
        recyclerView7.setLayoutManager(new LinearLayoutManager(this));
        cartList = new ArrayList<>();
        cartAdapter = new CartItemAdapter(this, cartList, userId);
        recyclerView7.setAdapter(cartAdapter);

        loadCartItems(); // Load dữ liệu giỏ hàng từ Firebase

        // Bắt sự kiện khi nhấn vào imgBack sẽ chuyển về màn hình trước đó
        imgBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Xử lý khi bấm nút Thanh Toán
        btnPayment.setOnClickListener(v -> proceedToCheckout());
    }

    // Chuyển sang màn hình thanh toán và truyền dữ liệu giỏ hàng
    private void proceedToCheckout() {
        if (cartList == null || cartList.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống, không thể thanh toán!", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<CartItem> items = new ArrayList<>();
        long totalPrice = 0;

        for (CartItem item : cartList) {
            if (item != null) {
                items.add(item);
                totalPrice += item.getPrice() * item.getQuantity();
            }
        }

        if (items.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm hợp lệ để thanh toán!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Tạo random phí ship từ (10000đ - 40000đ)
        long ship = ThreadLocalRandom.current().nextLong(10000, 40000);

        // Tạo intent sang PaymentActivity và truyền dữ liệu
        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
        intent.putExtra("Items", items);
        intent.putExtra("totalPrice", totalPrice);
        intent.putExtra("ship", ship);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        startActivity(intent);
    }

    // Lấy ID người dùng từ bộ nhớ cục bộ (SharedPreferences)
    private String getUserIdFromLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

    // Tải Cart từ Firebase
    private void loadCartItems() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CartItem item = dataSnapshot.getValue(CartItem.class);
                    if (item != null) {
                        cartList.add(item);
                    }
                }
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice(); // Cập nhật tổng tiền
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Lỗi khi tải giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm cập nhật tổng tiền hiển thị trên giao diện
    public void updateTotalPrice() {
        long totalPrice = 0;
        for (CartItem item : cartList) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        priceTotal.setText(String.format("%,d đ", totalPrice));
    }
}
