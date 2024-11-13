package store.model.domain;

import store.constant.ProductType;

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

    public ProductType isPromotion() {
        if (promotion.equals("null")) {
            return ProductType.REGULAR;
        }
        return ProductType.PROMOTION;
    }
}
