package store;

import store.controller.StoreController;
import store.model.repository.ProductRepository;
import store.model.repository.PromotionRepository;
import store.model.service.ReceiptCalculationService;
import store.model.service.StoreService;
import store.view.InputView;
import store.view.OutputView;

public class Application {
    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepository();
        PromotionRepository promotionRepository = new PromotionRepository();

        StoreService storeService = new StoreService(productRepository, promotionRepository);
        ReceiptCalculationService receiptCalculationService = new ReceiptCalculationService();

        InputView inputView = new InputView();
        OutputView outputView = new OutputView();
        StoreController storeController = new StoreController(inputView, outputView, storeService, receiptCalculationService);

        storeController.run();
    }
}
