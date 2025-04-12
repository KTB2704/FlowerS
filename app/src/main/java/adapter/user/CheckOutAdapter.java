package adapter.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowers.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import domain.CartItem;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.MyViewHolder> {
    private Context context;
    private List<CartItem> checkoutList;

    public CheckOutAdapter(Context context, List<CartItem> checkoutList) {
        this.context = context;
        this.checkoutList = checkoutList;
    }

    @NonNull
    @Override
    public CheckOutAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.checkout_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutAdapter.MyViewHolder holder, int position) {
        CartItem item = checkoutList.get(position);

        long price = item.getPrice();
        int quantity = item.getQuantity();
        long totalItemPrice = price * quantity;

        holder.txtName.setText(item.getName());
        holder.txtPrice.setText(String.format("%,dđ", item.getPrice()));
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));
        holder.txtPriceTotal.setText(String.format("%,dđ", totalItemPrice));
        Picasso.get().load(item.getImg()).into(holder.imgF);
    }

    @Override
    public int getItemCount() {
        return checkoutList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtQuantity, txtPriceTotal;
        ImageView imgF;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPriceTotal = itemView.findViewById(R.id.txtPriceTotal);
            imgF = itemView.findViewById(R.id.imgF);
        }
    }
}
