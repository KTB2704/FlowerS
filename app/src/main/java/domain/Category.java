package domain;

public class Category {
    private String Id;
    private String image;
    private String name;

    public Category() {
    }

    public Category(String id, String image, String name) {
        this.Id = id;
        this.image = image;
        this.name = name;
    }

    public String getId() {
        return this.Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
