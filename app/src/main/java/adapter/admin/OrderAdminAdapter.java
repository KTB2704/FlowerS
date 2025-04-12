package adapter.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowers.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import adapter.user.ProductsOrderAdapter;
import domain.CartItem;
import domain.Orders;

public class OrderAdminAdapter extends RecyclerView.Adapter<OrderAdminAdapter.MyViewHolder> {

    private Context context;
    private List<Orders> orderList;

    public OrderAdminAdapter(Context context, List<Orders> orderList) {
        this.context = context;
        this.orderList = orderList;
    }
    @NonNull
    @Override
    public OrderAdminAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.action_admin_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdminAdapter.MyViewHolder holder, int position) {
        Orders orders = orderList.get(position);
        holder.OrderId.setText(orders.getOrderId());
        holder.DateAndDay.setText(orders.getOrderTime());
        holder.Name.setText(orders.getName());
        holder.Location.setText(orders.getAddress());
        holder.Phone.setText(orders.getPhoneNumber());
        holder.AllTotalPrice.setText(String.format("%,dđ", orders.getAllTotalPrice()));
        holder.TotalPrice.setText(String.format("%,dđ", orders.getTotalPrice()));
        holder.ShipPrice.setText(String.format("%,dđ", orders.getShip()));
        holder.AllTotalPrice1.setText(String.format("%,dđ", orders.getAllTotalPrice()));

        // Tùy theo trạng thái đơn hàng hiển thị nút hành động tương ứng
        if (orders.getStatus().equals("Chờ xác nhận")) {
            holder.btnActionAdmin.setText("Xác nhận");
        } else if (orders.getStatus().equals("Đã xác nhận")) {
            holder.btnActionAdmin.setText("Giao hàng");
        }

        // Hiển thị danh sách sản phẩm trong đơn hàng
        List<CartItem> productList = new ArrayList<>(orders.getCartItems().values());

        // Setup RecyclerView cho danh sách sản phẩm trong đơn
        ProductsOrderAdapter productAdapter = new ProductsOrderAdapter(context, productList);
        holder.recyclerView11.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView11.setAdapter(productAdapter);
        holder.recyclerView11.setNestedScrollingEnabled(true);

        // Xử lý va chạm khi cuộn trong RecyclerView lồng nhau
        holder.recyclerView11.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        // Gọi hàm xử lý hành động khi nhấn nút
        holder.btnActionAdmin.setOnClickListener(v -> handleOrderAction(orders, position));
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView OrderId, DateAndDay, btnActionAdmin, AllTotalPrice, AllTotalPrice1, TotalPrice, ShipPrice, Name, Phone, Location;
        RecyclerView recyclerView11;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            OrderId = itemView.findViewById(R.id.OrderId);
            DateAndDay = itemView.findViewById(R.id.DateAndDay);
            btnActionAdmin = itemView.findViewById(R.id.btnActionAdmin);
            recyclerView11 = itemView.findViewById(R.id.RecycleView11);
            AllTotalPrice = itemView.findViewById(R.id.AllTotalPrice);
            AllTotalPrice1 = itemView.findViewById(R.id.AllTotalPrice1);
            TotalPrice = itemView.findViewById(R.id.TotalPrice);
            ShipPrice = itemView.findViewById(R.id.ShipPrice);
            Name = itemView.findViewById(R.id.Name);
            Phone = itemView.findViewById(R.id.Phone);
            Location = itemView.findViewById(R.id.Location);
        }
    }

    // Xử lý khi admin nhấn nút "Xác nhận" hoặc "Giao hàng"
    private void handleOrderAction(Orders orders, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (orders.getStatus().equals("Chờ xác nhận")) {
            builder.setTitle("Xác nhận đơn hàng");
            builder.setMessage("Bạn có chắc chắn muốn xác nhận đơn hàng này không?");
            builder.setPositiveButton("Có", (dialog, which) -> confirmOrderAdmin(orders, position));
            builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
        }else if (orders.getStatus().equals("Đã xác nhận")) {
            builder.setTitle("Xác nhận giao hàng");
            builder.setMessage("Bạn có chắc chắn muốn xác nhận giao đơn hàng này không?");
            builder.setPositiveButton("Có", (dialog, which) -> deliveryOrderAdmin(orders, position));
            builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Xử lý xác nhận giao hàng
    private void deliveryOrderAdmin(Orders orders, int position) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("orders");
        String userId = orders.getUserId();
        String orderId = orders.getOrderId();

        // Xóa đơn hàng khỏi danh sách "accepted"
        databaseRef.child("admin").child("accepted").child(orderId).removeValue();
        databaseRef.child("users").child(userId).child("pending").child(orderId).removeValue();

        // Cập nhật trạng thái đơn hàng thành đang giao hàng
        orders.setStatus("Đang giao hàng");

        // Chuyển đơn hàng sang nhánh "delivery"
        databaseRef.child("admin").child("delivery").child(orderId).setValue(orders);
        databaseRef.child("users").child(userId).child("delivery").child(orderId).setValue(orders);

        // Xóa item khỏi danh sách hiển thị và cập nhật giao diện
        orderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderList.size());

        Toast.makeText(context, "Đơn hàng đang được giao!", Toast.LENGTH_SHORT).show();
    }

    // Xử lý xác nhận đơn hàng
    private void confirmOrderAdmin(Orders orders, int position) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("orders");
        String userId = orders.getUserId();
        String orderId = orders.getOrderId();

        // Xóa khỏi danh sách "pending"
        databaseRef.child("admin").child("pending").child(orderId).removeValue();
        databaseRef.child("users").child(userId).child("pending").child(orderId).removeValue();

        // Cập nhật trạng thái đơn hàng thành đã xác nhận
        orders.setStatus("Đã xác nhận");

        // Chuyển đơn hàng sang nhánh "accepted" của admin còn user sang nhánh "pending"
        databaseRef.child("admin").child("accepted").child(orderId).setValue(orders);
        databaseRef.child("users").child(userId).child("pending").child(orderId).setValue(orders);

        // Xóa item khỏi danh sách hiển thị và cập nhật giao diện
        orderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderList.size());

        Toast.makeText(context, "Đơn hàng đã được xác nhận", Toast.LENGTH_SHORT).show();
    }

}
