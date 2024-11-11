package store.config;

import store.model.domain.Product;
import store.model.domain.ProductType;
import store.model.domain.Promotion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataInitializer {
    public static final String REGULAR = "null";

    private static DataInitializer instance;

    private DataInitializer() {
    }

    public static DataInitializer getInstance() {
        if (instance == null) {
            instance = new DataInitializer();
        }
        return instance;
    }

    public ProductsData loadProducts(String filePath) throws IOException {
        Map<ProductType, Map<String, Product>> info = new LinkedHashMap<>();
        Map<ProductType, Map<String, Integer>> stock = new HashMap<>();

        try (BufferedReader br = readFile(filePath)) {
            processProduct(br, info, stock);
        }

        return new ProductsData(info, stock);
    }

    public Map<String, Promotion> loadPromotions(String filePath) throws IOException {
        Map<String, Promotion> promotions = new LinkedHashMap<>();

        try (BufferedReader br = readFile(filePath)) {
            processPromotion(br, promotions);
        }

        return promotions;
    }

    private BufferedReader readFile(String filePath) throws IOException {
        return new BufferedReader(new FileReader(filePath));
    }

    private void processProduct(BufferedReader br,
                                Map<ProductType, Map<String, Product>> info,
                                Map<ProductType, Map<String, Integer>> stock) throws IOException {
        Map<String, Product> regularProducts = new LinkedHashMap<>();
        Map<String, Product> promotionProducts = new LinkedHashMap<>();
        Map<String, Integer> regularStocks = new HashMap<>();
        Map<String, Integer> promotionStocks = new HashMap<>();

        br.readLine(); // 헤더 생략
        String line;
        while ((line = br.readLine()) != null) {
            ProductData productData = parseProduct(line);
            addProduct(productData, regularProducts, promotionProducts, regularStocks, promotionStocks);
        }

        info.put(ProductType.REGULAR, regularProducts);
        info.put(ProductType.PROMOTION, promotionProducts);
        stock.put(ProductType.REGULAR, regularStocks);
        stock.put(ProductType.PROMOTION, promotionStocks);
    }

    private ProductData parseProduct(String line) {
        String[] data = line.split(",");
        String name = data[0];
        int price = Integer.parseInt(data[1]);
        int quantity = Integer.parseInt(data[2]);
        String promotion = data[3];
        return new ProductData(name, price, quantity, promotion);
    }

    private void addProduct(ProductData productData,
                            Map<String, Product> regularProducts,
                            Map<String, Product> promotionProducts,
                            Map<String, Integer> regularStocks,
                            Map<String, Integer> promotionStocks) {
        Product product = new Product(productData.name(), productData.price(), productData.promotion());

        if (productData.promotion().equals(REGULAR)) {
            regularProducts.put(productData.name(), product);
            regularStocks.put(productData.name(), productData.quantity());
            return;
        }

        regularProducts.computeIfAbsent(productData.name(), n -> new Product(n, productData.price(), REGULAR));
        regularStocks.putIfAbsent(productData.name(), 0);
        promotionProducts.put(productData.name(), product);
        promotionStocks.put(productData.name(), productData.quantity());
    }

    private void processPromotion(BufferedReader br, Map<String, Promotion> promotions) throws IOException {
        br.readLine(); // 헤더 생략
        String line;
        while ((line = br.readLine()) != null) {
            Promotion promotion = parsePromotion(line);
            promotions.put(promotion.getName(), promotion);
        }
    }

    private Promotion parsePromotion(String line) {
        String[] data = line.split(",");
        String name = data[0];
        int buy = Integer.parseInt(data[1]);
        int get = Integer.parseInt(data[2]);
        LocalDate startDate = LocalDate.parse(data[3]);
        LocalDate endDate = LocalDate.parse(data[4]);
        return new Promotion(name, buy, get, startDate, endDate);
    }
}