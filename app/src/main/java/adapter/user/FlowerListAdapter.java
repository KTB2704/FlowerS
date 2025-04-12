package adapter.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowers.R;
import com.squareup.picasso.Picasso;
import com.example.flowers.user.FlowerDetailActivity;

import java.util.List;

import domain.Flowers;

public class FlowerListAdapter extends RecyclerView.Adapter<FlowerListAdapter.MyViewHolder> {
    private Context context;

    public FlowerListAdapter(Context context, List<Flowers> flowersList) {
        this.context = context;
        this.flowersList = flowersList;
    }

    private List<Flowers> flowersList;

    @NonNull
    @Override
    public FlowerListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_list_flowers, parent, false);
        return new FlowerListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlowerListAdapter.MyViewHolder holder, int position) {
        Flowers flower = flowersList.get(position);
        holder.txtNameF.setText(flower.getName());
        holder.txtPriceF.setText( flower.getPrice() + "đ");
        Picasso.get()
                .load(flower.getImg())
                .into(holder.imgListF);

        /**
         * Gán sự kiện khi click vào item trong danh sách flowers.
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
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return flowersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtNameF, txtPriceF;
        ImageView imgListF;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNameF = itemView.findViewById(R.id.txtNameF);
            txtPriceF = itemView.findViewById(R.id.txtPriceF);
            imgListF = itemView.findViewById(R.id.imgListF);
        }
    }
}
