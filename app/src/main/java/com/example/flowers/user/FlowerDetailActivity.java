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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import adapter.user.BestFlowersAdapter;
import domain.CartItem;
import domain.Flowers;

public class FlowerDetailActivity extends AppCompatActivity {
    private TextView txtName, txtPrice, txtDesc, btnBack, btnAdd;
    private ImageView imgF, imgBack, imgCart;
    private RecyclerView recyclerView6;

    private BestFlowersAdapter bestFlowersAdapter;
    private List<Flowers> flowersList;

    private DatabaseReference databaseReference;
    private String flowerName, img;
    private long price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flower_detail);

        txtName = findViewById(R.id.txtName);
        txtPrice = findViewById(R.id.txtPrice);
        txtDesc = findViewById(R.id.txtDes);
        imgF = findViewById(R.id.imgF);
        imgBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnUpdate);
        imgCart = findViewById(R.id.imgCart);
        recyclerView6 = findViewById(R.id.recycleView6);

        // Setup RecyclerView cho best flowers
        recyclerView6.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        flowersList = new ArrayList<>();
        bestFlowersAdapter = new BestFlowersAdapter(this, flowersList);
        recyclerView6.setAdapter(bestFlowersAdapter);

        // Nhận dữ liệu được truyền từ màn hình trước qua intent
        flowerName = getIntent().getStringExtra("name");
        price = getIntent().getLongExtra("price", 0);
        img = getIntent().getStringExtra("img");
        String desc = getIntent().getStringExtra("description");

        // Hiển thị dữ liệu lên UI
        txtName.setText(flowerName);
        txtPrice.setText(price + "đ");
        txtDesc.setText(desc);
        Picasso.get().load(img).into(imgF);

        // Bắt sự kiện khi nhấn vào imgBack sẽ chuyển về màn hình trước đó
        imgBack.setOnClickListener(v->{
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Bắt sự kiện khi nhấn vào giỏ hàng sẽ chuyển sang CartActivity
        imgCart.setOnClickListener(v -> {
            Intent intent = new Intent(FlowerDetailActivity.this, CartActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            startActivity(intent);
        });

        initBestF(); // Load bestFlower từ Firebase

        // Thêm hoa vào giỏ hàng khi nhấn nút "Thêm"
        btnAdd.setOnClickListener(v -> addToCart());

    }

    // Lấy ID người dùng từ bộ nhớ cục bộ (SharedPreferences)
    private String getUserIdFromLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

    // Hàm xử lý thêm sản phẩm vào giỏ hàng
    private void addToCart() {
        String userId = getUserIdFromLocal();
        if (userId == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

         databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId).child(flowerName);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int newQuantity = 1;
                if (snapshot.exists()) {
                    int currentQuantity = snapshot.child("quantity").getValue(Integer.class);
                    newQuantity = currentQuantity + 1;
                }

                CartItem cartItem = new CartItem(flowerName, img, price, newQuantity);
                databaseReference.setValue(cartItem)
                        .addOnSuccessListener(aVoid -> Toast.makeText(FlowerDetailActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(FlowerDetailActivity.this, "Lỗi khi thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FlowerDetailActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Lỗi khi tải dữ liệu", error.toException());
            }
        });

    }



}