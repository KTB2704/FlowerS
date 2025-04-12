package adapter.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowers.user.CancelUserActivity;
import com.example.flowers.user.PendingUserActivity;
import com.example.flowers.R;
import com.example.flowers.user.SuccessUserActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import domain.CartItem;
import domain.Orders;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private Context context;
    private List<Orders> orderList;

    public OrderAdapter(Context context, List<Orders> orderList) {
        this.context = context;
        this.orderList = orderList;
    }


    @NonNull
    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.action_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.MyViewHolder holder, int position) {
        Orders orders = orderList.get(position);
        holder.OrderId.setText(orders.getOrderId());
        holder.Status.setText(orders.getStatus());
        holder.DateAndDay.setText(orders.getOrderTime());

        // Gán trạng thái và hành động dựa theo trạng thái đơn hàng
        if (orders.getStatus().equals("Chờ xác nhận")) {
            holder.btnAction.setText("Hủy đơn");
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.pending));
        } else if (orders.getStatus().equals("Đã hủy")) {
            holder.btnAction.setText("Mua lại");
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.cancel));
            holder.btnAction.setBackgroundResource(R.drawable.bg_button_action2);
            holder.btnAction.setTextColor(ContextCompat.getColor(context, R.color.pending));
        } else if (orders.getStatus().equals("Đã xác nhận")) {
            holder.btnAction.setText("Hủy đơn");
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.accept));
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.pending));
        }else if (orders.getStatus().equals("Đang giao hàng")) {
            holder.btnAction.setText("Giao thành công");
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.delivery));
            holder.btnAction.setBackgroundResource(R.drawable.bg_button_action2);
            holder.btnAction.setTextColor(ContextCompat.getColor(context, R.color.pending));
        } else if (orders.getStatus().equals("Hoàn thành"))  {
            holder.btnAction.setText("Đánh giá");
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.success));
            holder.btnAction.setBackgroundResource(R.drawable.bg_button_action2);
            holder.btnAction.setTextColor(ContextCompat.getColor(context, R.color.pending));
        }

        holder.AllTotalPrice.setText(String.format("%,dđ", orders.getAllTotalPrice()));

        // Lấy danh sách sản phẩm từ đơn hàng
        List<CartItem> productList = new ArrayList<>(orders.getCartItems().values());

        // Setup RecyclerView cho danh sách sản phẩm trong đơn
        ProductsOrderAdapter productAdapter = new ProductsOrderAdapter(context, productList);
        holder.recyclerView10.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView10.setAdapter(productAdapter);
        holder.recyclerView10.setNestedScrollingEnabled(true);

        // Xử lý va chạm khi cuộn trong RecyclerView lồng nhau
        holder.recyclerView10.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        // Gọi hàm xử lý hành động khi nhấn nút
        holder.btnAction.setOnClickListener(v -> handleOrderAction(orders, position));

    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView OrderId, Status, DateAndDay, btnAction, AllTotalPrice;
        RecyclerView recyclerView10;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            OrderId = itemView.findViewById(R.id.OrderId);
            Status = itemView.findViewById(R.id.Status);
            DateAndDay = itemView.findViewById(R.id.DateAndDay);
            btnAction = itemView.findViewById(R.id.btnAction);
            recyclerView10 = itemView.findViewById(R.id.RecyclerView10);
            AllTotalPrice = itemView.findViewById(R.id.AllTotalPrice);
        }
    }

    // Hàm xử lý hành động khi người dùng tương tác
    private void handleOrderAction(Orders orders, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (orders.getStatus().equals("Chờ xác nhận")) {
            builder.setTitle("Xác nhận hủy đơn");
            builder.setMessage("Bạn có chắc chắn muốn hủy đơn hàng này không?");
            builder.setPositiveButton("Có", (dialog, which) -> cancelOrder(orders, position));
            builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
        } else if (orders.getStatus().equals("Đã hủy")) {
            builder.setTitle("Xác nhận mua lại");
            builder.setMessage("Bạn có muốn mua lại đơn hàng này không?");
            builder.setPositiveButton("Có", (dialog, which) -> reOrder(orders, position));
            builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
        }
        else if (orders.getStatus().equals("Đang giao hàng")) {
            builder.setTitle("Xác nhận giao hàng");
            builder.setMessage("Bạn có chắc chắn xác nhận giao hàng thành công");
            builder.setPositiveButton("Có", (dialog, which) -> deliveryOrder(orders, position));
            builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // Giao hàng thành công
    private void deliveryOrder(Orders orders, int position) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("orders");
        String userId = orders.getUserId();
        String orderId = orders.getOrderId();

        // Xóa đơn hàng khỏi nhánh "delivery" trong admin và user
        databaseRef.child("admin").child("delivery").child(orderId).removeValue();
        databaseRef.child("users").child(userId).child("delivery").child(orderId).removeValue();

        // Cập nhật trạng thái đơn hàng thành "Hoàn thành"
        orders.setStatus("Hoàn thành");

        // Chuyển đơn hàng sang nhánh "success"
        databaseRef.child("admin").child("success").child(orderId).setValue(orders);
        databaseRef.child("users").child(userId).child("success").child(orderId).setValue(orders);

        // Xóa item khỏi danh sách hiển thị và cập nhật giao diện
        orderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderList.size());

        Toast.makeText(context, "Đơn hàng đã giao thành công", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, SuccessUserActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    // Mua lại đơn hàng đã bị hủy
    private void reOrder(Orders orders, int position) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("orders");
        String userId = orders.getUserId();
        String orderId = orders.getOrderId();

        // Xóa đơn hàng khỏi nhánh "canceled"
        databaseRef.child("admin").child("canceled").child(orderId).removeValue();
        databaseRef.child("users").child(userId).child("canceled").child(orderId).removeValue();

        // Cập nhật trạng thái thành "Chờ xác nhận"
        orders.setStatus("Chờ xác nhận");

        // Chuyển đơn hàng vào nhánh "pending"
        databaseRef.child("admin").child("pending").child(orderId).setValue(orders);
        databaseRef.child("users").child(userId).child("pending").child(orderId).setValue(orders);

        // Xóa item khỏi danh sách hiển thị và cập nhật giao diện
        orderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderList.size());

        Toast.makeText(context, "Đơn hàng đã được đặt lại!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, PendingUserActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    // Hủy đơn hàng đang ở trạng thái "Chờ xác nhận"
    private void cancelOrder(Orders orders, int position) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("orders");
        String userId = orders.getUserId();
        String orderId = orders.getOrderId();

        // Xóa đơn hàng khỏi nhánh "pending"
        databaseRef.child("admin").child("pending").child(orderId).removeValue();
        databaseRef.child("users").child(userId).child("pending").child(orderId).removeValue();

        // Cập nhật trạng thái thành "Đã hủy"
        orders.setStatus("Đã hủy");

        // Thêm đơn hàng vào nhánh "canceled"
        databaseRef.child("admin").child("canceled").child(orderId).setValue(orders);
        databaseRef.child("users").child(userId).child("canceled").child(orderId).setValue(orders);

        // Xóa item khỏi danh sách hiển thị và cập nhật giao diện
        orderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderList.size());

        Toast.makeText(context, "Đơn hàng đã hủy!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, CancelUserActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

}
