package decorator;

public abstract class Drink {
    public String description;
    public float price = 0.0f;

    public float getPrice() {
        return price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public abstract float cost();


}
