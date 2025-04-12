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

import java.util.List;

import domain.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {
    private Context context;
    private List<Category> categoryList;
    private OnItemClickListener listener;;
    private Integer backgroundColor;



    public CategoryAdapter(Context context, List<Category> categoryList, OnItemClickListener listener, Integer backgroundColor) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
        this.backgroundColor = backgroundColor;
    }



     // Interface để xử lý sự kiện click khi người dùng chọn một category
    public interface OnItemClickListener {
        void onItemClick(Category category, int position);
    }

    // Cho phép gán lại listener từ bên ngoài
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_cate, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.catName.setText(category.getName());

        // Lấy ID hình ảnh từ tên file drawable
        int imageResId = context.getResources().getIdentifier(category.getImage(), "drawable", context.getPackageName());
        holder.catView.setImageResource(imageResId);

        // Nếu có màu nền được chỉ định → áp dụng
        if (backgroundColor != null) {
            holder.itemView.setBackgroundColor(backgroundColor);
        }

        // Gán sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(category, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView catName;
        ImageView catView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            catName = itemView.findViewById(R.id.catText);
            catView = itemView.findViewById(R.id.catView);
        }
    }
}
