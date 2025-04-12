package adapter.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowers.R;

import java.util.List;

import domain.Orders;

public class OrderAdminHistoryAdapter extends RecyclerView.Adapter<OrderAdminHistoryAdapter.MyViewHolder> {
    private Context context;
    private List<Orders> orderList;

    public OrderAdminHistoryAdapter(Context context, List<Orders> orderList) {
        this.context = context;
        this.orderList = orderList;
    }
    @NonNull
    @Override
    public OrderAdminHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_admin_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdminHistoryAdapter.MyViewHolder holder, int position) {
        Orders orders = orderList.get(position);
        holder.OrderId.setText(orders.getOrderId());
        holder.DateAndDay.setText(orders.getOrderTime());
        holder.Name.setText(orders.getName());
        holder.Location.setText(orders.getAddress());
        holder.Phone.setText(orders.getPhoneNumber());
        holder.AllTotalPrice.setText(String.format("%,dđ", orders.getAllTotalPrice()));
        holder.Status.setText(orders.getStatus());

        // Hiển thị số lượng sản phẩm trong đơn
        int productQuantity = orders.getCartItems().size();
        holder.productQuantity.setText(String.valueOf(productQuantity));

        // Đổi màu chữ của trạng thái theo từng trạng thái cụ thể
        if (orders.getStatus().equals("Đang giao hàng")) {
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.delivery));
        } else if (orders.getStatus().equals("Hoàn thành"))  {
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.success));
        } else if (orders.getStatus().equals("Đã hủy"))  {
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.cancel));
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView OrderId, DateAndDay, AllTotalPrice,Name, Phone, Location, Status, productQuantity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            OrderId = itemView.findViewById(R.id.OrderId);
            DateAndDay = itemView.findViewById(R.id.DateAndDay);
            AllTotalPrice = itemView.findViewById(R.id.AllTotalPrice);
            Name = itemView.findViewById(R.id.Name);
            Phone = itemView.findViewById(R.id.Phone);
            Location = itemView.findViewById(R.id.Location);
            Status = itemView.findViewById(R.id.Status);
            productQuantity = itemView.findViewById(R.id.productQuantity);
        }
    }
}
