package domain;

import androidx.annotation.NonNull;

public class Flowers {
    private String name, description, img;
    private long price;
    private boolean favourite, bestFlower;
    private String categoryId;

    public Flowers(String name, String description, String img, long price, boolean favourite, boolean bestFlower, String categoryId) {
        this.name = name;
        this.description = description;
        this.img = img;
        this.price = price;
        this.favourite = favourite;
        this.bestFlower = bestFlower;
        this.categoryId = categoryId;
    }

    public Flowers() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isBestFlower() {
        return bestFlower;
    }

    public void setBestFlower(boolean bestFlower) {
        this.bestFlower = bestFlower;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
