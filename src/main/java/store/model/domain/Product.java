package store.model.domain;

public class Product {
    private final String name;
    private final int price;
    private final String promotion;

    public Product(String name, int price, String promotion) {
        this.name = name;
        this.price = price;
        this.promotion = promotion;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getPromotion() {
        return promotion;
    }
}
