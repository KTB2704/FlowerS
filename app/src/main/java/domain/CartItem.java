package domain;

import android.widget.ImageView;

import java.io.Serializable;


// Lớp CartItem đại diện cho một sản phẩm trong giỏ hàng
// implements Serializable để cho phép đối tượng này có thể được truyền qua Intent
public class CartItem implements Serializable {
    private String name;
    private String img;
    private long price;
    private int quantity;

    public CartItem() {
    }

    public CartItem(String name, String img, long price, int quantity) {
        this.name = name;
        this.img = img;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
