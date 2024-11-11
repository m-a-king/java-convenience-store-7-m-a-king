package store.model.service;

import store.model.domain.ShoppingCartCheck;
import store.model.ReceiptTotals;
import store.constant.Discounts;

import java.util.List;

public class ReceiptCalculationService {

    public ReceiptTotals calculateTotals(List<ShoppingCartCheck> shoppingCartChecks, boolean isMembership) {
        ReceiptTotals totals = new ReceiptTotals();

        for (ShoppingCartCheck dto : shoppingCartChecks) {
            int price = dto.getProductPrice() * dto.getRequestCount();
            totals.totalCount += dto.getRequestCount();
            totals.totalPrice += price;
            totals.regularPrice += dto.getRegularCount() * dto.getProductPrice();
        }

        totals.giftPrice = calculateGiftDiscount(shoppingCartChecks);
        totals.membershipPrice = calculateMembershipDiscount(totals.regularPrice, isMembership);

        return totals;
    }

    private int calculateGiftDiscount(List<ShoppingCartCheck> shoppingCartChecks) {
        int giftPrice = 0;
        for (ShoppingCartCheck dto : shoppingCartChecks) {
            if (dto.getFreeCount() > 0 && dto.isActivePromotion()) {
                giftPrice -= dto.getProductPrice() * dto.getFreeCount();
            }
        }
        return giftPrice;
    }

    private int calculateMembershipDiscount(int regularPrice, boolean isMembership) {
        if (isMembership) {
            double discountPrice = regularPrice * -Discounts.MEMBERSHIP_DISCOUNT_RATE;
            if (Math.abs(discountPrice) > Discounts.MAX_MEMBERSHIP_DISCOUNT) {
                return -Discounts.MAX_MEMBERSHIP_DISCOUNT;
            }
            return (int) discountPrice;
        }
        return 0;
    }
}