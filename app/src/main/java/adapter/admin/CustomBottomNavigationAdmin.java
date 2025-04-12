package adapter.admin;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.flowers.admin.AdminActivity;
import com.example.flowers.admin.NoficationAdminActivity;
import com.example.flowers.admin.OrderAdminActivity;
import com.example.flowers.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomBottomNavigationAdmin {

    // Thiết lập sự kiện và animation cho thanh điều hướng bottom dành cho admin
    public static void setupBottomNavigationAdmin(final Activity activity, BottomNavigationView bottomNavigationView) {

        // Tải animation "bounce" để tạo hiệu ứng khi click
        final Animation bounceAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);

        // Bắt sự kiện khi người dùng chọn một item trong BottomNavigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int clickedItemId = item.getItemId();

            // Tìm view tương ứng với item đó trong bottom navigation
            View selectedView = bottomNavigationView.findViewById(clickedItemId);

            if (selectedView != null) {
                // Nếu tìm thấy view → chạy hiệu ứng bounce → sau đó chuyển activity
                selectedView.startAnimation(bounceAnim);
                selectedView.postDelayed(() -> changeActivity(activity, clickedItemId), bounceAnim.getDuration());
            } else {
                changeActivity(activity, clickedItemId);
            }
            return true;
        });
    }

    // Hàm xử lý việc chuyển sang Activity tương ứng dựa vào ID của item được chọn
    private static void changeActivity(Activity currentActivity, int selectedItemId) {

        // Nếu đang ở đúng activity thì không làm gì cả
        if (isActivitySelected(currentActivity, selectedItemId)) {
            return;
        }

        Intent intent = null;

        // Xác định activity cần chuyển đến dựa trên item ID
        if (selectedItemId == R.id.nav_order) {
            intent = new Intent(currentActivity, OrderAdminActivity.class);
        } else if (selectedItemId == R.id.nav_nofi) {
            intent = new Intent(currentActivity, NoficationAdminActivity.class);
        } else if (selectedItemId == R.id.nav_home) {
            intent = new Intent(currentActivity, AdminActivity.class);
        }

        // Nếu intent không null → thực hiện chuyển activity
        if (intent != null) {
            currentActivity.startActivity(intent);
            currentActivity.overridePendingTransition(0, 0);
            currentActivity.finish();
        }
    }

    // Hàm kiểm tra xem activity hiện tại có đúng với tab đã chọn hay không
    private static boolean isActivitySelected(Activity currentActivity, int selectedItemId) {
        if (selectedItemId == R.id.nav_order && currentActivity instanceof OrderAdminActivity) {
            return true;
        } else if (selectedItemId == R.id.nav_nofi && currentActivity instanceof NoficationAdminActivity) {
            return true;
        } else if (selectedItemId == R.id.nav_home && currentActivity instanceof AdminActivity) {
            return true;
        }
        return false;
    }
}
