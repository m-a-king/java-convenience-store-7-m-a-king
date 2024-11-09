package store.model.repository;

import store.model.domain.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ProductRepository {
    private final Map<String, Map<String, Product>> info = new LinkedHashMap<>();
    private final Map<String, Map<String, Integer>> stock = new HashMap<>();

    public void loadProducts(String filePath) throws IOException {
        Map<String, Product> regularProducts = new LinkedHashMap<>();
        Map<String, Product> promotionProducts = new LinkedHashMap<>();
        Map<String, Integer> regularStocks = new HashMap<>();
        Map<String, Integer> promotionStock = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // 헤더 생략

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                int price = Integer.parseInt(data[1]);
                int quantity = Integer.parseInt(data[2]);
                String promotion = data[3];

                Product product = new Product(name, price, promotion);

                if (promotion.equals("null")) {
                    regularProducts.put(name, product);
                    regularStocks.put(name, quantity);
                } else {
                    promotionProducts.put(name, product);
                    promotionStock.put(name, quantity);
                }
            }

            info.put("regular", regularProducts);
            info.put("promotion", promotionProducts);
            stock.put("regular", regularStocks);
            stock.put("promotion", promotionStock);
        }
    }

    public Product findProductByTypeAndName(String type, String name) {
        return info.get(type).getOrDefault(name, null);
    }

    public List<Product> findAllProduct() {
        return info.values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .toList();
    }

    public int findStockByProduct(Product product) {
        if (product.getPromotion().equals("null")) {
            return stock.get("regular").getOrDefault(product.getName(), 0);
        }
        return stock.get("promotion").getOrDefault(product.getName(), 0);
    }

    public boolean reduceStock(String type, String name, int quantity) {
        Map<String, Integer> typeStock = stock.get(type);
        if (typeStock == null || !typeStock.containsKey(name)) {
            return false;
        }

        int currentStock = typeStock.get(name);
        if (currentStock >= quantity) {
            typeStock.put(name, currentStock - quantity);
            return true;
        }
        return false;
    }

}