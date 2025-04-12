package adapter.user;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowers.user.CartActivity;
import com.example.flowers.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import domain.CartItem;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.MyViewHolder> {
    private Context context;
    private List<CartItem> cartList;
    private String userId;

    // Constructor: truyền context, danh sách sản phẩm trong giỏ và ID người dùng
    public CartItemAdapter(Context context, List<CartItem> cartList, String userId) {
        this.context = context;
        this.cartList = cartList;
        this.userId = userId;
    }
    @NonNull
    @Override
    public CartItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payment_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.MyViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        holder.txtName.setText(item.getName());
        holder.txtPrice.setText(item.getPrice() + "đ");
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));

        Picasso.get().load(item.getImg()).into(holder.imgF);

        // Tham chiếu đến Firebase: "Cart/userId/tên hoa"
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId).child(item.getName());

        // Xử lý nút tăng số lượng
        holder.imgPlus.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            databaseReference.child("quantity").setValue(newQuantity);
            item.setQuantity(newQuantity);
            notifyItemChanged(position);
            updateTotalPrice();
        });

        // Xử lý nút giảm số lượng
        holder.imgMinus.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity > 0) {
                databaseReference.child("quantity").setValue(newQuantity);
                item.setQuantity(newQuantity);
                notifyItemChanged(position);
                updateTotalPrice();
            } else {
                // Nếu số lượng = 0, hỏi người dùng có muốn xóa không
                showDeleteConfirmationDialog(databaseReference, item.getName(), position);
            }
        });
        // Click vào item → hỏi xác nhận xóa khỏi giỏ
        holder.itemView.setOnClickListener(v -> showDeleteCartItemConfirmationDialog(databaseReference, item, position));
    }


    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtQuantity;
        ImageView imgF, imgMinus, imgPlus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.quantity);
            imgF = itemView.findViewById(R.id.imgF);
            imgMinus = itemView.findViewById(R.id.imgMinus);
            imgPlus = itemView.findViewById(R.id.imgPlus);
        }
    }

    // Hiển thị hộp thoại xác nhận xóa khi số lượng về 0
    private void showDeleteConfirmationDialog(DatabaseReference databaseReference, String name, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            databaseReference.removeValue();
            cartList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Đã xóa sản phẩm!", Toast.LENGTH_SHORT).show();
            updateTotalPrice();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Cập nhật tổng tiền trong giỏ bằng cách gọi hàm từ CartActivity
    private void updateTotalPrice() {
        if (context instanceof CartActivity) {
            ((CartActivity) context).updateTotalPrice();
        }
    }

    // Hộp thoại xác nhận khi người dùng click vào item để xóa
    private void showDeleteCartItemConfirmationDialog(DatabaseReference databaseReference, CartItem item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            databaseReference.removeValue();
            cartList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Đã xóa sản phẩm!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
