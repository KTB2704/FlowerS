package adapter.user;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowers.user.FlowerDetailActivity;
import com.example.flowers.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import domain.Flowers;

public class BestFlowersAdapter extends RecyclerView.Adapter<BestFlowersAdapter.MyViewHolder> {
    private Context context;
    private List<Flowers> flowersList;

    public BestFlowersAdapter(Context context, List<Flowers> flowersList) {
        this.context = context;
        this.flowersList = flowersList;
    }

    @NonNull
    @Override
    public BestFlowersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_best_flowers, parent, false);
        return new BestFlowersAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BestFlowersAdapter.MyViewHolder holder, int position) {
        Flowers flower = flowersList.get(position);
        holder.textName.setText(flower.getName());
        holder.textPrice.setText( flower.getPrice() + "đ");
        Picasso.get()
                .load(flower.getImg())
                .into(holder.imgF);

        /**
         * Gán sự kiện khi click vào item trong danh sách best flowers.
         * Tạo Intent chuyển sang màn hình chi tiết FlowerDetailActivity
         * Gửi dữ liệu  qua intent
         */
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FlowerDetailActivity.class);
            intent.putExtra("name", flower.getName());
            intent.putExtra("price", flower.getPrice());
            intent.putExtra("img", flower.getImg());
            intent.putExtra("description", flower.getDescription());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return flowersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textName, textPrice;
        ImageView imgF;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textPrice = itemView.findViewById(R.id.textPrice);
            imgF = itemView.findViewById(R.id.imgF);
        }
    }
}
