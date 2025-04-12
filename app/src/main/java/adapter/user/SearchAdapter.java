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

import com.example.flowers.user.FlowerDetailActivity;
import com.example.flowers.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import domain.Flowers;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    private Context context;
    private List<Flowers> filteredList;

    public SearchAdapter(Context context, List<Flowers> filteredList) {
        this.context = context;
        this.filteredList = filteredList;
    }

    // Cập nhật danh sách hiển thị và thông báo cho adapter rằng dữ liệu đã thay đổi
    public void updateList(List<Flowers> newList) {
        this.filteredList = new ArrayList<>(newList); // Gán danh sách mới vào danh sách hiện tại
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_search, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder holder, int position) {
        Flowers flowers = filteredList.get(position);
        holder.nameS.setText(flowers.getName());
        holder.priceS.setText(flowers.getPrice() + "đ");
        Picasso.get()
                .load(flowers.getImg())
                .into(holder.imgS);

        /**
         * Gán sự kiện khi click vào item trong danh sách flowers được tìm ở thanh search.
         * Tạo Intent chuyển sang màn hình chi tiết FlowerDetailActivity
         * Gửi dữ liệu  qua intent
         */
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FlowerDetailActivity.class);
            intent.putExtra("name", flowers.getName());
            intent.putExtra("price", flowers.getPrice());
            intent.putExtra("img", flowers.getImg());
            intent.putExtra("description", flowers.getDescription());
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameS,priceS;
        ImageView imgS;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameS = itemView.findViewById(R.id.nameS);
            priceS = itemView.findViewById(R.id.priceS);
            imgS = itemView.findViewById(R.id.imgS);
        }
    }
}
