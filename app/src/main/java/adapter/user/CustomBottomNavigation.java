package adapter.user;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.flowers.user.MainActivity;
import com.example.flowers.R;
import com.example.flowers.user.WidgetUserActivity;
import com.example.flowers.user.SearchUserActivity;
import com.example.flowers.user.NoficationUserActivity;
import com.example.flowers.user.ProfileUserActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomBottomNavigation {

    // Thiết lập sự kiện và animation cho thanh điều hướng bottom dành cho user
    public static void setupBottomNavigation(final Activity activity, BottomNavigationView bottomNavigationView) {

        // Tải animation bounce khi click vào icon
        final Animation bounceAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);

        // Lắng nghe sự kiện click vào từng item của bottom navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int clickedItemId = item.getItemId();

            // Lấy view tương ứng với item vừa được chọn
            View selectedView = bottomNavigationView.findViewById(clickedItemId);

            // Nếu tìm được view → chạy animation và chuyển activity sau đó
            if (selectedView != null) {
                selectedView.startAnimation(bounceAnim);
                selectedView.postDelayed(() -> changeActivity(activity, clickedItemId), bounceAnim.getDuration());
            } else {
                changeActivity(activity, clickedItemId);
            }
            return true;
        });
    }

    // Hàm chuyển sang activity tương ứng với icon được chọn
    private static void changeActivity(Activity currentActivity, int selectedItemId) {

        // Kiểm tra nếu đang ở đúng activity đó rồi thì không làm gì cả
        if (isActivitySelected(currentActivity, selectedItemId)) {
            return;
        }

        Intent intent = null;

        // Gán intent tương ứng với từng ID item
        if (selectedItemId == R.id.nav_home) {
            intent = new Intent(currentActivity, MainActivity.class);
        } else if (selectedItemId == R.id.nav_widget) {
            intent = new Intent(currentActivity, WidgetUserActivity.class);
        } else if (selectedItemId == R.id.nav_search) {
            intent = new Intent(currentActivity, SearchUserActivity.class);
        } else if (selectedItemId == R.id.nav_nofi) {
            intent = new Intent(currentActivity, NoficationUserActivity.class);
        } else if (selectedItemId == R.id.nav_users) {
            intent = new Intent(currentActivity, ProfileUserActivity.class);
        }

        // Nếu có intent hợp lệ → chuyển activity
        if (intent != null) {
            currentActivity.startActivity(intent);
            currentActivity.overridePendingTransition(0, 0);
            currentActivity.finish();
        }
    }

    // Kiểm tra xem activity hiện tại có tương ứng với item được chọn hay không
    private static boolean isActivitySelected(Activity currentActivity, int selectedItemId) {
        if (selectedItemId == R.id.nav_home && currentActivity instanceof MainActivity) {
            return true;
        } else if (selectedItemId == R.id.nav_widget && currentActivity instanceof WidgetUserActivity) {
            return true;
        } else if (selectedItemId == R.id.nav_search && currentActivity instanceof SearchUserActivity) {
            return true;
        } else if (selectedItemId == R.id.nav_nofi && currentActivity instanceof NoficationUserActivity) {
            return true;
        } else if (selectedItemId == R.id.nav_users && currentActivity instanceof ProfileUserActivity) {
            return true;
        }
        return false;
    }
}
