package com.example.flowers.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowers.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import domain.Category;
import domain.Flowers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProductInfoAdminActivity extends AppCompatActivity {
    private ImageView btnChoose, imgBack, btnSetting;
    private EditText etName, etPrice, etDes;
    private Spinner spinCate;
    private TextView btnDelete, btnUpdate;

    private DatabaseReference databaseReference;
    private List<Category> categoryList = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();
    private String flowerId = null;
    private String oldImageUrl = null;

    private Bitmap selectedBitmap = null;
    private String uploadedImageUrl = null;

    private final String CLIENT_ID = "b9d9edd0a155e29";
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_info_admin);

        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etDes = findViewById(R.id.etDes);
        btnChoose = findViewById(R.id.btnChoose);
        spinCate = findViewById(R.id.spinCate);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        imgBack = findViewById(R.id.imgBack);
        btnSetting = findViewById(R.id.btnSetting);

        setupImagePicker(); // Khởi tạo xử lý chọn ảnh

        loadInfoFlower();  // Load dữ liệu sản phẩm được chọn để hiển thị

        // Sự kiện khi nhấn nút xóa
        btnDelete.setOnClickListener(v -> deleteFlower());

        // Sự kiện khi nhấn nút sửa
        btnUpdate.setOnClickListener(v -> updateFlower());

        // Sự kiện chọn ảnh mới
        btnChoose.setOnClickListener(v -> chooseImage());

        // Bắt sự kiện khi nhấn vào imgBack sẽ chuyển về màn hình trước
        imgBack.setOnClickListener(v->
                {
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                });

        // Bắt sự kiện khi nhấn vào btnSetting sẽ chuyển sang ProfileAdminActivity
        btnSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileAdminActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    // Cấu hình chức năng chọn ảnh từ thư viện
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            btnChoose.setImageBitmap(selectedBitmap);
                            uploadToImgur(selectedBitmap); // Tải ảnh lên Imgur
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    // Chọn ảnh
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh mới"));
    }

    // Tải ảnh lên Imgur bằng OkHttp
    private void uploadToImgur(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("image", encodedImage)
                .build();

        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/image")
                .addHeader("Authorization", "Client-ID " + CLIENT_ID)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("IMGUR_UPLOAD", "Upload ảnh thất bại!", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);
                        uploadedImageUrl = json.getJSONObject("data").getString("link");

                        Log.d("IMGUR_UPLOAD", "Upload thành công! URL: " + uploadedImageUrl);
                    } catch (JSONException e) {
                        Log.e("IMGUR_UPLOAD", "Lỗi xử lý JSON response!", e);
                    }
                } else {
                    Log.e("IMGUR_UPLOAD", "Response không thành công: " + response.code());
                }
            }
        });
    }


    // Cập nhật thông tin sản phẩm
    private void updateFlower() {
        String name = etName.getText().toString().trim();
        String description = etDes.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        String categoryId = categoryList.get(spinCate.getSelectedItemPosition()).getId();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        long price;
        try {
            price = Long.parseLong(priceText.replace("đ", "").replace(",", "").trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu người dùng không chọn ảnh mới thì giữ lại ảnh cũ
        String finalImageUrl = (uploadedImageUrl != null) ? uploadedImageUrl : oldImageUrl;

        Flowers flower = new Flowers(name, description, finalImageUrl, price, false, false, categoryId);

        databaseReference = FirebaseDatabase.getInstance().getReference("flowers");
        databaseReference.child(flowerId).setValue(flower)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                    loadInfoFlower();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show());
    }

    // Xóa sản phẩm
    private void deleteFlower() {
        databaseReference = FirebaseDatabase.getInstance().getReference("flowers");
        if (flowerId != null) {
            databaseReference.child(flowerId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Xóa thất bại!", Toast.LENGTH_SHORT).show());
        }
    }

    // Tải thông tin sản phẩm từ Intent được truyền qua và category từ Firebase
    private void loadInfoFlower() {

        // Lấy thông tin sản phẩm từ Intent
        flowerId = getIntent().getStringExtra("id");
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String description = getIntent().getStringExtra("description");
        oldImageUrl = getIntent().getStringExtra("img");
        String categoryId = getIntent().getStringExtra("categoryId");

        etName.setText(name);
        etPrice.setText(price);
        etDes.setText(description);
        Picasso.get().load(oldImageUrl).into(btnChoose);

        databaseReference = FirebaseDatabase.getInstance().getReference("Category");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                categoryNames.clear();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Category category = item.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                        categoryNames.add(category.getName());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        ProductInfoAdminActivity.this,
                        android.R.layout.simple_spinner_item,
                        categoryNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinCate.setAdapter(adapter);

                // Đặt spinner về danh mục tương ứng với sản phẩm đang chỉnh sửa
                for (int i = 0; i < categoryList.size(); i++) {
                    if (categoryList.get(i).getId().equals(categoryId)) {
                        spinCate.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductInfoAdminActivity.this, "Không tải được danh mục!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
