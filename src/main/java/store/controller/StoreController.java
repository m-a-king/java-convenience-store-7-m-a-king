package store.controller;

import store.Message.ErrorMessage;
import store.model.domain.ShoppingCartCheck;
import store.model.ReceiptTotals;
import store.model.service.StoreService;
import store.model.service.ReceiptCalculationService;
import store.util.StringParser;
import store.view.InputView;
import store.view.OutputView;

import java.util.List;
import java.util.Map;

public class StoreController {
    private final InputView inputView;
    private final OutputView outputView;
    private final StoreService storeService;
    private final ReceiptCalculationService receiptCalculationService;

    public StoreController(InputView inputView, OutputView outputView, StoreService storeService, ReceiptCalculationService receiptCalculationService) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.storeService = storeService;
        this.receiptCalculationService = receiptCalculationService;
    }

    public void run() {
        do {
            initializeView();
            Map<String, Integer> shoppingCart = askPurchase();
            List<ShoppingCartCheck> shoppingCartChecks = processShoppingCart(shoppingCart);
            boolean isMembership = processMembership();

            ReceiptTotals totals = receiptCalculationService.calculateTotals(shoppingCartChecks, isMembership);
            outputView.printReceipt(shoppingCartChecks, totals);

            storeService.reduceProductStocks(shoppingCartChecks);
        } while (askAdditionalPurchase());
    }

    private void initializeView() {
        outputView.printHelloMessage();
        outputView.printProducts(storeService.getAllProductDtos());
    }

    private Map<String, Integer> askPurchase() {
        while (true) {
            try {
                String item = inputView.readItem();
                Map<String, Integer> shoppingCart = StringParser.parseToMap(item);
                storeService.checkStock(shoppingCart);
                return shoppingCart;
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private List<ShoppingCartCheck> processShoppingCart(Map<String, Integer> shoppingCart) {
        List<ShoppingCartCheck> shoppingCartChecks = storeService.checkShoppingCart(shoppingCart);
        shoppingCartChecks.forEach(this::processPromotionForItem);
        return shoppingCartChecks;
    }

    private void processPromotionForItem(ShoppingCartCheck item) {
        while (item.isCanReceiveAdditionalFree()) {
            String decision = inputView.readPromotionDecision(item.getProductName());
            if (decision.equals("Y")) {
                item.acceptFree();
                return;
            }
            if (decision.equals("N")) {
                return;
            }
            System.out.println(ErrorMessage.GENERIC_ERROR.getMessage());
        }

        while (item.isActivePromotion() && item.getFullPriceCount() > 0) {
            String decision = inputView.readFullPriceDecision(item.getProductName(), item.getFullPriceCount());
            if (decision.equals("Y")) {
                return;
            }
            if (decision.equals("N")) {
                item.rejectFullPrice();
                return;
            }
            System.out.println(ErrorMessage.GENERIC_ERROR.getMessage());
        }
    }

    private boolean processMembership() {
        while (true) {
            String decision = inputView.readMembershipDecision();
            if (decision.equals("Y")) {
                return true;
            }
            if (decision.equals("N")) {
                return false;
            }
            System.out.println(ErrorMessage.GENERIC_ERROR.getMessage());
        }
    }

    private boolean askAdditionalPurchase() {
        while (true) {
            String decision = inputView.readAdditionalPurchaseDecision();
            if (decision.equals("Y")) {
                System.out.println();
                return true;
            }
            if (decision.equals("N")) {
                return false;
            }
            System.out.println(ErrorMessage.GENERIC_ERROR.getMessage());
        }
    }
}