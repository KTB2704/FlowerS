package com.example.flowers.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flowers.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class AddFlowerAdminActivity extends AppCompatActivity {

    private Spinner spinCate;
    private List<Category> categoryList = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();
    private String selectedCategoryId = "";

    private EditText etName, etDes, etPrice;
    private ImageView btnChoose, imgBack, btnSetting;
    private TextView btnAdd;

    // Ảnh được chọn dưới dạng bitmap và link ảnh sau khi upload lên Imgur
    private Bitmap selectedBitmap = null;
    private String uploadedImageUrl = null;

    private final String CLIENT_ID = "b9d9edd0a155e29";

    // Biến để nhận kết quả chọn ảnh từ bộ nhớ máy
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_flower_admin);

        spinCate = findViewById(R.id.spinCate);
        etName = findViewById(R.id.etName);
        etDes = findViewById(R.id.etDes);
        etPrice = findViewById(R.id.etPrice);
        btnChoose = findViewById(R.id.btnChoose);
        btnAdd = findViewById(R.id.btnUpdate);
        imgBack = findViewById(R.id.imgBack);
        btnSetting = findViewById(R.id.btnSetting);

        // Cấu hình launcher để chọn ảnh từ thiết bị
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            btnChoose.setImageBitmap(selectedBitmap); // Hiển thị ảnh đã chọn
                            uploadToImgur(selectedBitmap); // Gọi hàm uploadToImgur
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        // Xử lý sự kiện chọn ảnh
        btnChoose.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh"));
        });

        // Xử lý sự kiện nhấn nút thêm sản phẩm
        btnAdd.setOnClickListener(v -> {
            if (uploadedImageUrl == null) {
                Toast.makeText(this, "Vui lòng chọn ảnh trước!", Toast.LENGTH_SHORT).show();
                return;
            }
            addFlower(uploadedImageUrl);
        });

        loadCategory(); // Load category từ Firebase

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

    // Hàm upload ảnh lên Imgur thông qua API
    private void uploadToImgur(Bitmap bitmap) {
        Log.d("ImgurUpload", "Đang tải ảnh lên Imgur...");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // Nén ảnh
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT); // Chuyển ảnh thành chuỗi base64

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("image", encodedImage)
                .build();

        // Gửi request và xử lý phản hồi
        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/image")
                .addHeader("Authorization", "Client-ID " + CLIENT_ID)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ImgurUpload", "Upload ảnh thất bại: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);
                        uploadedImageUrl = json.getJSONObject("data").getString("link");

                        Log.d("ImgurUpload", "Upload thành công! URL ảnh: " + uploadedImageUrl);
                    } catch (JSONException e) {
                        Log.e("ImgurUpload", "Lỗi JSON: " + e.getMessage());
                    }
                } else {
                    Log.e("ImgurUpload", "Phản hồi không thành công: " + response.message());
                }
            }
        });
    }

    // Hàm thêm sản phẩm mới vào Firebase
    private void addFlower(String uploadedImageUrl) {
        String name = etName.getText().toString().trim();
        String description = etDes.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        long price;
        try {
            price = Long.parseLong(priceText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá phải là số!", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("flowers");
        databaseReference.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(AddFlowerAdminActivity.this, "Sản phẩm đã tồn tại", Toast.LENGTH_SHORT).show();
                } else {
                    Flowers flower = new Flowers(name, description, uploadedImageUrl, price, false, false, selectedCategoryId);

                    String id = databaseReference.push().getKey();
                    databaseReference.child(id).setValue(flower).addOnSuccessListener(unused -> {
                        Toast.makeText(AddFlowerAdminActivity.this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AddFlowerAdminActivity.this, "Lỗi ", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Load danh sách category từ Firebase để hiển thị trong Spinner
    private void loadCategory() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Category");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                categoryList.clear();
                categoryNames.clear();

                for (DataSnapshot item : snapshot.getChildren()) {
                    Category category = item.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                        categoryNames.add(category.getName());
                    }
                }

                // Tạo adapter cho spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        AddFlowerAdminActivity.this,
                        android.R.layout.simple_spinner_item,
                        categoryNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinCate.setAdapter(adapter);

                // Lưu ID của danh mục được chọn
                spinCate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCategoryId = categoryList.get(position).getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AddFlowerAdminActivity.this, "Không thể tải danh mục!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
