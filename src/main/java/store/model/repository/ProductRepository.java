package store.model.repository;

import store.model.domain.Product;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ProductRepository {
    private final Map<String, Map<String, Product>> products = new LinkedHashMap<>();

    public void loadProducts(String filePath) throws IOException {
        Map<String, Product> regularProducts = new LinkedHashMap<>();
        Map<String, Product> promotionProducts = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // 헤더 생략

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                int price = Integer.parseInt(data[1]);
                String promotion = data[3];

                Product product = new Product(name, price, promotion);

                if (promotion.equals("null")) {
                    regularProducts.put(name, product);
                } else {
                    promotionProducts.put(name, product);
                }
            }

            products.put("regular", regularProducts);
            products.put("promotion", promotionProducts);
        }
    }

    public Product findByTypeAndName(String type, String name) {
        return products.get(type).getOrDefault(name, null);
    }

    public List<Product> findAll() {
        return products.values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .toList();
    }
}