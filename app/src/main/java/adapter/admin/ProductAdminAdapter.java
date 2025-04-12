package adapter.admin;

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
import com.example.flowers.admin.ProductInfoAdminActivity;
import com.example.flowers.user.FlowerDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import adapter.user.FlowerListAdapter;
import domain.Flowers;

public class ProductAdminAdapter extends RecyclerView.Adapter<ProductAdminAdapter.MyViewHolder> {
    private Context context;
    private List<Flowers> flowersList;
    private Map<Flowers, String> flowerKeyMap;


    public ProductAdminAdapter(Context context, List<Flowers> flowersList, Map<Flowers, String> flowerKeyMap ) {
        this.context = context;
        this.flowersList = flowersList;
        this.flowerKeyMap = flowerKeyMap;
    }

    @NonNull
    @Override
    public ProductAdminAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.products_order_admin_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdminAdapter.MyViewHolder holder, int position) {
        Flowers flower = flowersList.get(position);
        holder.txtName.setText(flower.getName());
        holder.txtPrice.setText( flower.getPrice() + "đ");
        Picasso.get()
                .load(flower.getImg())
                .into(holder.imgF);

        // Khi nhấn nút "btnNext" -> mở màn hình ProductInfoAdminActivity để xem/chỉnh sửa chi tiết sản phẩm
        holder.btnNext.setOnClickListener(v -> {

            // Lấy ID từ map (key tương ứng trong Firebase)
            String flowerId = flowerKeyMap.get(flower);

            // Tạo Intent và truyền dữ liệu sang ProductInfoAdminActivity
            Intent intent = new Intent(context, ProductInfoAdminActivity.class);
            intent.putExtra("id", flowerId);
            intent.putExtra("name", flower.getName());
            intent.putExtra("price", flower.getPrice() + "đ");
            intent.putExtra("img", flower.getImg());
            intent.putExtra("description", flower.getDescription());
            intent.putExtra("categoryId", flower.getCategoryId());
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
        TextView txtName, txtPrice;
        ImageView imgF, btnNext;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imgF = itemView.findViewById(R.id.imgF);
            btnNext = itemView.findViewById(R.id.btnNext);
        }
    }
}
