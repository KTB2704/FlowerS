package com.example.flowers.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;

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
import adapter.user.SearchAdapter;
import domain.Flowers;

public class SearchUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView5;
    private SearchAdapter searchAdapter;
    private SearchView searchView;
    private List<Flowers> flowerList;
    private DatabaseReference databaseReference;
    private ImageView imgCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);

        // Cài đặt navigation bottom user
        BottomNavigationView bnv = findViewById(R.id.BottomNavView);
        bnv.setSelectedItemId(R.id.nav_search);
        CustomBottomNavigation.setupBottomNavigation(this, bnv);


        imgCart = findViewById(R.id.imgCart);
        recyclerView5 = findViewById(R.id.recycleView5);
        searchView = findViewById(R.id.searchView);


        searchView.setIconifiedByDefault(false); // Không thu gọn thanh tìm kiếm
        searchView.setFocusable(true); // Cho phép nhận focus
        searchView.requestFocusFromTouch(); // Tự động focus khi mở Activity

        // Setup RecyclerView cho thanh search để hiển thị flowers
        recyclerView5.setLayoutManager(new LinearLayoutManager(this));
        flowerList = new ArrayList<>();
        searchAdapter = new SearchAdapter(this, flowerList);
        recyclerView5.setAdapter(searchAdapter);
        recyclerView5.setVisibility(View.GONE);

        // Bắt sự kiện khi nhấn vào giỏ hàng sẽ chuyển sang CartActivity
        imgCart.setOnClickListener(v -> {
            Intent intent = new Intent(SearchUserActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Truy cập tới node "flowers" trong Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("flowers");

        fetchFlowers(); // Load flowers từ Firebase

        // Lắng nghe sự thay đổi nội dung trong SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterResults(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterResults(newText);
                return false;
            }
        });

    }

    // Hàm để lấy danh sách hoa từ Firebase
    private void fetchFlowers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                flowerList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Flowers flowers = dataSnapshot.getValue(Flowers.class);
                    if (flowers != null) {
                        flowerList.add(flowers);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Hàm lọc kết quả dựa vào chuỗi người dùng nhập vào
    private void filterResults(String query) {
        if (query.isEmpty()) {
            recyclerView5.setVisibility(View.GONE);
            searchAdapter.updateList(new ArrayList<>());
            return;
        }

        List<Flowers> filteredList = new ArrayList<>();
        for (Flowers flowers : flowerList) {
            if (flowers.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(flowers);
            }
        }

        if (filteredList.isEmpty()) {
            recyclerView5.setVisibility(View.GONE);
        } else {
            recyclerView5.setVisibility(View.VISIBLE);
            searchAdapter.updateList(filteredList);
        }
    }


}


