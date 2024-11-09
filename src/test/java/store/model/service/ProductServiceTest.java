package store.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.dto.ProductWithStockDto;
import store.model.domain.Product;
import store.model.repository.ProductRepository;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductServiceTest {
    public static final String TEST_PRODUCT_FILE_PATH = "src/main/resources/testproducts.md";

    private ProductService productService;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() throws IOException {
        productRepository = new ProductRepository();
        productService = new ProductService(productRepository);
        productService.loadProducts(TEST_PRODUCT_FILE_PATH);
    }

    @Test
    @DisplayName("모든 상품 데이터를 정확하게 로드하고 확인한다")
    void loadProducts_ShouldContainAllTestProducts() {
        List<ProductWithStockDto> allProductDtos = productService.getAllProductDtos();

        // DTO 데이터 확인
        assertProductDto(allProductDtos, "콜라", 1000, 10, "탄산2+1");
        assertProductDto(allProductDtos, "콜라", 1000, 10, "null");
        assertProductDto(allProductDtos, "사이다", 1000, 8, "탄산2+1");
        assertProductDto(allProductDtos, "사이다", 1000, 7, "null");
        assertProductDto(allProductDtos, "오렌지주스", 1800, 9, "MD추천상품");
        assertProductDto(allProductDtos, "탄산수", 1200, 5, "탄산2+1");
        assertProductDto(allProductDtos, "물", 500, 10, "null");
        assertProductDto(allProductDtos, "비타민워터", 1500, 6, "null");
        assertProductDto(allProductDtos, "감자칩", 1500, 5, "반짝할인");
        assertProductDto(allProductDtos, "감자칩", 1500, 5, "null");
        assertProductDto(allProductDtos, "초코바", 1200, 5, "MD추천상품");
        assertProductDto(allProductDtos, "초코바", 1200, 5, "null");
        assertProductDto(allProductDtos, "에너지바", 2000, 5, "null");
        assertProductDto(allProductDtos, "정식도시락", 6400, 8, "null");
        assertProductDto(allProductDtos, "컵라면", 1700, 1, "MD추천상품");
        assertProductDto(allProductDtos, "컵라면", 1700, 10, "null");
    }

    private void assertProductDto(List<ProductWithStockDto> dtos, String name, int price, int stock, String promotion) {
        ProductWithStockDto dto = dtos.stream()
                .filter(productDto -> productDto.name().equals(name) && productDto.promotion().equals(promotion))
                .findFirst()
                .orElse(null);

        assertThat(dto).isNotNull();
        assertThat(dto.price()).isEqualTo(price);
        assertThat(dto.stock()).isEqualTo(stock);
        assertThat(dto.promotion()).isEqualTo(promotion);
    }

    @Test
    @DisplayName("재고 감소가 정상적으로 이루어지고, true를 반환한다")
    void reduceProductStock_ShouldReturnTrue_WhenStockIsReduced() {
        // given
        String name = "콜라";
        int price = 1000;
        String type = "promotion";
        Product cola = new Product(name, price, type);

        int quantity = 5;

        // when
        boolean result = productService.reduceProductStock(cola, quantity);

        // then
        assertThat(result).isTrue();
        assertThat(productRepository.findStockByProduct(cola)).isEqualTo(5);
    }
}