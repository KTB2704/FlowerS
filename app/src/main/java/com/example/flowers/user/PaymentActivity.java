package com.example.flowers.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowers.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import adapter.user.CheckOutAdapter;
import domain.CartItem;
import domain.Orders;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView recyclerView8;
    private CheckOutAdapter checkOutAdapter;
    private ArrayList<CartItem> Items;
    private TextView priceTotal, priceShip, priceTotalAll;
    private EditText txtName, txtLocation, txtPhone;
    private Button btnPayment;

    private ImageView imgBack;
    private long ship;
    private long totalPrice;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        recyclerView8 = findViewById(R.id.recyclerView8);
        btnPayment = findViewById(R.id.btnPayment);
        priceTotal = findViewById(R.id.priceTotal);
        priceShip = findViewById(R.id.priceShip);
        priceTotalAll = findViewById(R.id.priceTotalAll);
        txtName = findViewById(R.id.txtName);
        txtLocation = findViewById(R.id.txtLocation);
        txtPhone = findViewById(R.id.txtPhone);
        imgBack = findViewById(R.id.imgBack);

        // Lấy danh sách sản phẩm được truyền qua từ CartActivity
        Items = (ArrayList<CartItem>) getIntent().getSerializableExtra("Items");
        if (Items == null) {
            Log.e("PaymentActivity", "Items bị NULL! Đặt danh sách rỗng.");
            Items = new ArrayList<>();
        }

        // Lấy tổng tiền và phí ship truyền qua từ CartActivity
        totalPrice = getIntent().getLongExtra("totalPrice", 0);
        ship = getIntent().getLongExtra("ship", 10000);
        long totalAllPrice = totalPrice + ship;

        // Hiển thị thông tin giá
        priceTotal.setText(formatCurrency(totalPrice));
        priceShip.setText(formatCurrency(ship));
        priceTotalAll.setText(formatCurrency(totalAllPrice));

        // Setup adapter để hiển thị danh sách sản phẩm trong Cart
        checkOutAdapter = new CheckOutAdapter(this, Items);
        recyclerView8.setLayoutManager(new LinearLayoutManager(this));
        recyclerView8.setAdapter(checkOutAdapter);

        // Bắt sự kiện khi nhấn vào imgBack sẽ chuyển về màn hình trước đó
        imgBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Lấy userId từ SharedPreferences
        userId = getUserIdFromLocal();
        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy tài khoản!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Xử lý khi bấm nút "Thanh toán"
        btnPayment.setOnClickListener(v -> placeOrder());
    }

    // Hàm định dạng tiền tệ theo dạng: 100,000đ
    private String formatCurrency(long amount) {
        return String.format("%,dđ", amount);
    }


    // Lấy ID người dùng từ bộ nhớ cục bộ (SharedPreferences)
    private String getUserIdFromLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

    // Hàm đặt đơn hàng
    private void placeOrder() {
        if (Items.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = txtName.getText().toString().trim();
        String phone = txtPhone.getText().toString().trim();
        String address = txtLocation.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tham chiếu tới node "orders" trong Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("orders");

        // Tạo orderId tự động cho người dùng
        String orderId = databaseRef.child("users").child(userId).child("pending").push().getKey();

        if (orderId == null) {
            Toast.makeText(this, "Lỗi khi tạo đơn hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thời gian hiện tại
        String orderTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        long totalAllPrice = totalPrice + ship;

        // Đưa danh sách sản phẩm vào dạng Map (key: tên sản phẩm)
        Map<String, CartItem> cartItemsMap = new HashMap<>();
        for (CartItem item : Items) {
            cartItemsMap.put(item.getName(), item);
        }


        Orders newOrder = new Orders(orderId, userId, "Chờ xác nhận", orderTime, name, phone, address, totalPrice, ship, totalAllPrice, cartItemsMap);

        // Ghi đơn hàng vào Firebase - user + admin
        databaseRef.child("users").child(userId).child("pending").child(orderId).setValue(newOrder);
        databaseRef.child("admin").child("pending").child(orderId).setValue(newOrder)
                .addOnSuccessListener(aVoid -> {
                    clearCart();
                    Toast.makeText(PaymentActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                })
                .addOnFailureListener(e -> Toast.makeText(PaymentActivity.this, "Đơn hàng đặt không thành công: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    // Hàm xóa giỏ hàng sau khi đã đặt hàng
    private void clearCart() {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
        cartRef.removeValue()
                .addOnSuccessListener(aVoid -> Log.d("Cart", "Đã xóa giỏ hàng sau khi đặt hàng"))
                .addOnFailureListener(e -> Log.e("Cart", "Lỗi khi xóa giỏ hàng: " + e.getMessage()));
    }
}
