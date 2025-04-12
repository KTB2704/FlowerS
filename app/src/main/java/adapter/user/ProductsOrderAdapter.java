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

// Adapter con của OrderAdapter và OrderAdminAdapter
public class ProductsOrderAdapter extends RecyclerView.Adapter<ProductsOrderAdapter.MyViewHolder> {
    private Context context;
    private List<CartItem> productList;

    public ProductsOrderAdapter(Context context, List<CartItem> productList) {
        this.context = context;
        this.productList = productList;
    }


    @NonNull
    @Override
    public ProductsOrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.products_order_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsOrderAdapter.MyViewHolder holder, int position) {
        CartItem item = productList.get(position);
        holder.txtName.setText(item.getName());
        holder.txtPrice.setText(String.format("%,dđ", item.getPrice()));
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));
        Picasso.get().load(item.getImg()).into(holder.imgF);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtQuantity;
        ImageView imgF;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            imgF = itemView.findViewById(R.id.imgF);
        }
    }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.setNestedScrollingEnabled(true);
    }

}
