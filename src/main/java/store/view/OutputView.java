package store.view;

import store.dto.ProductWithStockDto;
import store.model.domain.ShoppingCartCheck;
import store.model.ReceiptTotals;
import store.constant.Discounts;
import store.constant.OutputFormats;
import store.constant.OutputMessages;
import store.constant.ReceiptLabels;

import java.util.List;

public class OutputView {

    public void printHelloMessage() {
        System.out.println(OutputMessages.WELCOME_MESSAGE);
    }

    public void printProducts(List<ProductWithStockDto> products) {
        System.out.println(OutputMessages.CURRENT_PRODUCTS_MESSAGE);
        products.forEach(this::printProduct);
    }

    public void printPurchasePrompt() {
        System.out.println(OutputMessages.PURCHASE_PROMPT_MESSAGE);
    }

    public void printMembershipPrompt() {
        System.out.println(OutputMessages.MEMBERSHIP_PROMPT_MESSAGE);
    }

    public void printPromotionMessage(ShoppingCartCheck dto) {
        System.out.printf(Discounts.ADDITIONAL_FREE_MESSAGE_FORMAT, dto.getProductName());
    }

    public void printRegularPriceMessage(ShoppingCartCheck dto) {
        System.out.printf(Discounts.REGULAR_PRICE_MESSAGE_FORMAT, dto.getProductName(), dto.getFullPriceCount());
    }

    public void printAdditionalPurchasePrompt() {
        System.out.println(OutputMessages.ADDITIONAL_PURCHASE_PROMPT_MESSAGE);
    }

    public void printReceipt(List<ShoppingCartCheck> shoppingCartChecks, ReceiptTotals totals) {
        printReceiptHeader();
        printReceiptItems(shoppingCartChecks);
        printReceiptGiftItems(shoppingCartChecks);
        printReceiptTotals(totals);
    }

    private void printProduct(ProductWithStockDto product) {
        String promotion = product.promotion();
        if (promotion.equals(Discounts.REGULAR)) {
            promotion = OutputMessages.EMPTY_STRING;
        }
        if (product.stock() == 0) {
            System.out.printf(OutputFormats.PRODUCT_OUT_OF_STOCK_FORMAT, product.name(), product.price(), OutputMessages.NO_STOCK, promotion);
            return;
        }
        System.out.printf(OutputFormats.PRODUCT_IN_STOCK_FORMAT, product.name(), product.price(), product.stock(), promotion);
    }

    private void printReceiptHeader() {
        System.out.println(ReceiptLabels.HEADER);
        System.out.printf(OutputFormats.HEADER_FORMAT, ReceiptLabels.PRODUCT_NAME, ReceiptLabels.COUNT, ReceiptLabels.PRICE);
    }

    private void printReceiptItems(List<ShoppingCartCheck> shoppingCartChecks) {
        for (ShoppingCartCheck dto : shoppingCartChecks) {
            int price = dto.getProductPrice() * dto.getRequestCount();
            System.out.printf(OutputFormats.ITEM_FORMAT, dto.getProductName(), dto.getRequestCount(), price);
        }
    }

    private void printReceiptGiftItems(List<ShoppingCartCheck> shoppingCartChecks) {
        List<ShoppingCartCheck> giftItems = shoppingCartChecks.stream()
                .filter(dto -> dto.getFreeCount() > 0)
                .filter(ShoppingCartCheck::isActivePromotion)
                .toList();

        if (!giftItems.isEmpty()) {
            System.out.println(ReceiptLabels.GIFT_HEADER);
            for (ShoppingCartCheck dto : giftItems) {
                System.out.printf(OutputFormats.GIFT_ITEM_FORMAT, dto.getProductName(), dto.getFreeCount());
            }
        }
    }

    private void printReceiptTotals(ReceiptTotals totals) {
        System.out.println(ReceiptLabels.FOOTER);
        System.out.printf(OutputFormats.ITEM_FORMAT, ReceiptLabels.TOTAL_LABEL, totals.totalCount, totals.totalPrice);

        String giftPriceDisplay = formatDiscount(totals.giftPrice);
        System.out.printf(OutputFormats.DISCOUNT_FORMAT, ReceiptLabels.EVENT_DISCOUNT_LABEL, giftPriceDisplay);

        String membershipPriceDisplay = formatDiscount(totals.membershipPrice);
        System.out.printf(OutputFormats.DISCOUNT_FORMAT, ReceiptLabels.MEMBERSHIP_DISCOUNT_LABEL, membershipPriceDisplay);

        int finalAmount = totals.totalPrice + totals.giftPrice + totals.membershipPrice;
        System.out.printf(OutputFormats.TOTAL_FORMAT, ReceiptLabels.FINAL_AMOUNT_LABEL, finalAmount);
    }

    private String formatDiscount(int discount) {
        if (discount == 0) return "-0";
        return String.format("%,d", discount);
    }
}