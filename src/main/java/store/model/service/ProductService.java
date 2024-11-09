package store.model.service;

import store.dto.ProductWithStockDto;
import store.model.domain.Product;
import store.model.repository.ProductRepository;

import java.io.IOException;
import java.util.List;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void loadProducts(String filePath) throws IOException {
        productRepository.loadProducts(filePath);
    }

    public List<ProductWithStockDto> getAllProductDtos() {
        return productRepository.findAllProduct().stream()
                .map(product -> new ProductWithStockDto(
                        product.getName(),
                        product.getPrice(),
                        productRepository.findStockByProduct(product),
                        product.getPromotion()
                ))
                .toList();
    }

    public boolean reduceProductStock(Product product, int quantity) {
        return productRepository.reduceStock(product, quantity);
    }
}