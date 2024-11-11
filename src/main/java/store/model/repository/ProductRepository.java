package store.model.repository;

import store.Message.ErrorMessage;
import store.config.DataInitializer;
import store.config.ProductsData;
import store.model.domain.Product;
import store.constant.ProductType;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductRepository {
    public static final String DEFAULT_PRODUCT_FILE_PATH = "src/main/resources/products.md";

    private final Map<ProductType, Map<String, Product>> info = new LinkedHashMap<>();
    private final Map<ProductType, Map<String, Integer>> stock = new HashMap<>();

    public ProductRepository(String filePath) {
        DataInitializer initializer = DataInitializer.getInstance();
        try {
            ProductsData productsData = initializer.loadProducts(filePath);
            this.info.putAll(productsData.info());
            this.stock.putAll(productsData.stock());
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessage.FILE_ERROR.getMessage(filePath));
        }
    }

    public ProductRepository() {
        this(DEFAULT_PRODUCT_FILE_PATH);
    }

    public List<Product> findAllInfo() {
        return info.values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .toList();
    }

    public Product findInfo(String name, ProductType productType) {
        Map<String, Product> typeInfo = info.get(productType);
        return typeInfo.getOrDefault(name, null);
    }

    public int findStock(String name, ProductType productType) {
        Map<String, Integer> typeStock = stock.get(productType);
        return typeStock.getOrDefault(name, 0);
    }

    public boolean reduceStock(String name, ProductType productType, int quantity) {
        Map<String, Integer> typeStocks = stock.get(productType);
        checkStock(name, typeStocks);

        int currentStock = typeStocks.get(name);
        if (currentStock < quantity) {
            return false;
        }

        typeStocks.put(name, currentStock - quantity);
        return true;
    }

    private static void checkStock(String name, Map<String, Integer> stock) {
        if (stock == null || !stock.containsKey(name)) {
            throw new IllegalStateException(ErrorMessage.NON_EXISTENT_PRODUCT.getMessage());
        }
    }
}