package store.config;

import store.model.domain.Product;
import store.constant.ProductType;

import java.util.Map;

public record ProductsData(Map<ProductType, Map<String, Product>> info,
                           Map<ProductType, Map<String, Integer>> stock
                         ) {
}
