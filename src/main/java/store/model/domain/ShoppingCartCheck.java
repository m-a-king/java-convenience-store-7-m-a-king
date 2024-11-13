package store.model.domain;

public class ShoppingCartCheck {
    private final String productName;
    private final int productPrice;
    private int requestCount;
    private int regularCount;
    private int promotionCount;
    private int fullPriceCount;
    private int freeCount;
    private boolean canReceiveAdditionalFree;
    private final boolean isActivePromotion;

    public ShoppingCartCheck(
            String productName,
            int productPrice,
            int requestCount,
            int regularCount,
            int promotionCount,
            int fullPriceCount,
            int freeCount,
            boolean canReceiveAdditionalFree,
            boolean isActivePromotion
    ) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.requestCount = requestCount;
        this.regularCount = regularCount;
        this.promotionCount = promotionCount;
        this.fullPriceCount = fullPriceCount;
        this.freeCount = freeCount;
        this.canReceiveAdditionalFree = canReceiveAdditionalFree;
        this.isActivePromotion = isActivePromotion;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public int getRegularCount() {
        return regularCount;
    }

    public int getPromotionCount() {
        return promotionCount;
    }

    public int getFullPriceCount() {
        return fullPriceCount;
    }

    public int getFreeCount() {
        return freeCount;
    }

    public boolean isCanReceiveAdditionalFree() {
        return canReceiveAdditionalFree;
    }

    public boolean isActivePromotion() {
        return isActivePromotion;
    }

    public void acceptFree() {
        requestCount++;
        promotionCount++;
        freeCount++;
        canReceiveAdditionalFree = false;
    }

    public void rejectFullPrice() {
        requestCount -= fullPriceCount;
        promotionCount -= fullPriceCount - regularCount;
        regularCount = Math.max(regularCount - fullPriceCount, 0);
        fullPriceCount = 0;
    }
}