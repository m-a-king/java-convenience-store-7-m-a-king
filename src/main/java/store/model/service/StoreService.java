package store.model.service;

import camp.nextstep.edu.missionutils.DateTimes;
import store.Message.ErrorMessage;
import store.dto.ProductWithStockDto;
import store.model.domain.ShoppingCartCheck;
import store.model.domain.Product;
import store.constant.ProductType;
import store.model.domain.Promotion;
import store.model.repository.ProductRepository;
import store.model.repository.PromotionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StoreService {
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

    public StoreService(ProductRepository productRepository, PromotionRepository promotionRepository) {
        this.productRepository = productRepository;
        this.promotionRepository = promotionRepository;
    }

    public List<ProductWithStockDto> getAllProductDtos() {
        return productRepository.findAllInfo().stream()
                .map(product -> new ProductWithStockDto(
                        product.getName(),
                        product.getPrice(),
                        productRepository.findStock(product.getName(), product.isPromotion()),
                        product.getPromotion()
                ))
                .toList();
    }

    public void checkStock(Map<String, Integer> shoppingCart) {
        shoppingCart.forEach((productName, requestedQuantity) -> {
            int totalAvailableStock = 0;

            totalAvailableStock += productRepository.findStock(productName, ProductType.PROMOTION);
            totalAvailableStock += productRepository.findStock(productName, ProductType.REGULAR);

            if (totalAvailableStock < requestedQuantity) {
                throw new IllegalArgumentException(ErrorMessage.EXCEEDS_STOCK.getMessage());
            }
        });
    }

    public List<ShoppingCartCheck> checkShoppingCart(Map<String, Integer> shoppingCart) {
        List<ShoppingCartCheck> shoppingCartChecks = new ArrayList<>();

        shoppingCart.forEach((productName, requestedQuantity) -> {
            ShoppingCartCheck shoppingCartCheck = processProductInCart(productName, requestedQuantity);
            if (shoppingCartCheck != null) {
                shoppingCartChecks.add(shoppingCartCheck);
            }
        });

        return shoppingCartChecks;
    }

    public void reduceProductStocks(List<ShoppingCartCheck> shoppingCartChecks) {
        shoppingCartChecks.forEach(shoppingCartCheck -> {
                    String productName = shoppingCartCheck.getProductName();
                    int promotionCount = shoppingCartCheck.getPromotionCount();
                    int regularCount = shoppingCartCheck.getRegularCount();
                    if (promotionCount > 0) {
                        productRepository.reduceStock(productName, ProductType.PROMOTION, promotionCount);
                    }
                    if (regularCount > 0) {
                        productRepository.reduceStock(productName, ProductType.REGULAR, regularCount);
                    }
                }
        );
    }

    private ShoppingCartCheck processProductInCart(String productName, int requestedQuantity) {
        Product product = getProductInfoOrThrow(productName);
        int productPrice = product.getPrice();
        Promotion promotion = getPromotionInfo(product);
        boolean isActivePromotion = isPromotionActive(promotion);

        int promotionStock = productRepository.findStock(productName, ProductType.PROMOTION);
        return createShoppingCartCheck(productName, productPrice, requestedQuantity, promotion, isActivePromotion, promotionStock);
    }

    private Product getProductInfoOrThrow(String productName) {
        Product product = getProductInfo(productName);
        if (product == null) {
            throw new IllegalStateException(ErrorMessage.NON_EXISTENT_PRODUCT.getMessage());
        }
        return product;
    }

    private ShoppingCartCheck createShoppingCartCheck(String productName, int productPrice, int requestedQuantity, Promotion promotion, boolean isActivePromotion, int promotionStock) {
        if (isPromotionUnavailable(promotion, promotionStock)) {
            return createNonPromotionShoppingCartCheck(productName, productPrice, requestedQuantity, isActivePromotion);
        }
        return createPromotionShoppingCartCheck(productName, productPrice, requestedQuantity, promotionStock, promotion, isActivePromotion);
    }

    private ShoppingCartCheck createNonPromotionShoppingCartCheck(String productName, int productPrice, int requestedQuantity, boolean isActivePromotion) {
        return new ShoppingCartCheck(
                productName,
                productPrice,
                requestedQuantity,
                requestedQuantity,
                0,
                requestedQuantity,
                0,
                false,
                isActivePromotion
        );
    }

    private ShoppingCartCheck createPromotionShoppingCartCheck(String productName, int productPrice, int requestedQuantity, int promotionStock, Promotion promotion, boolean isActivePromotion) {
        int buy = promotion.getBuy();
        int get = promotion.getGet();

        int regularCount = Math.max(requestedQuantity - promotionStock, 0);
        int promotionCount = requestedQuantity - regularCount;

        int fullPriceCount = calculateFullPriceCount(promotionCount, buy, get, regularCount);
        int freeCount = calculateFreeCount(promotionCount, buy, get);
        boolean canReceiveAdditionalFree = calculateAdditionalFree(requestedQuantity, buy, get, regularCount, promotionStock);

        return new ShoppingCartCheck(productName, productPrice, requestedQuantity, regularCount, promotionCount, fullPriceCount, freeCount, canReceiveAdditionalFree, isActivePromotion);
    }

    private Product getProductInfo(String productName) {
        Product product = productRepository.findInfo(productName, ProductType.PROMOTION);
        if (product == null) {
            product = productRepository.findInfo(productName, ProductType.REGULAR);
        }
        return product;
    }

    private Promotion getPromotionInfo(Product product) {
        if (product != null && product.getPromotion() != null) {
            return promotionRepository.findByName(product.getPromotion());
        }
        return null;
    }

    private boolean isPromotionUnavailable(Promotion promotion, int promotionStock) {
        return promotion == null || promotionStock == 0;
    }

    private boolean isPromotionActive(Promotion promotion) {
        return promotion != null && promotion.isActive(DateTimes.now().toLocalDate());
    }

    private int calculateFullPriceCount(int promotionCount, int buy, int get, int regularCount) {
        return promotionCount % (buy + get) + regularCount;
    }

    private int calculateFreeCount(int promotionCount, int buy, int get) {
        return promotionCount / (buy + get);
    }

    private boolean calculateAdditionalFree(int requestedQuantity, int buy, int get, int regularCount, int promotionStock) {
        return ((requestedQuantity % (buy + get)) == buy) && regularCount == 0 && promotionStock > requestedQuantity;
    }
}