package store.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.Message.ErrorMessage;
import store.dto.ProductWithStockDto;
import store.dto.ShoppingCartCheck;
import store.model.repository.ProductRepository;
import store.model.repository.PromotionRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class StoreServiceTest {
    private StoreService storeService;

    @BeforeEach
    void setUp() {
        ProductRepository productRepository = new ProductRepository();
        PromotionRepository promotionRepository = new PromotionRepository();
        storeService = new StoreService(productRepository, promotionRepository);
    }

    @Test
    @DisplayName("모든 상품 데이터를 정확하게 로드하고 확인한다")
    void loadProducts_ShouldContainAllTestProducts() {
        List<ProductWithStockDto> allProductDtos = storeService.getAllProductDtos();

        // DTO 데이터 확인
        assertProductDto(allProductDtos, "콜라", 1000, 10, "탄산2+1");
        assertProductDto(allProductDtos, "콜라", 1000, 10, "null");
        assertProductDto(allProductDtos, "사이다", 1000, 8, "탄산2+1");
        assertProductDto(allProductDtos, "사이다", 1000, 7, "null");
        assertProductDto(allProductDtos, "오렌지주스", 1800, 9, "MD추천상품");
        assertProductDto(allProductDtos, "오렌지주스", 1800, 0, "null");
        assertProductDto(allProductDtos, "탄산수", 1200, 5, "탄산2+1");
        assertProductDto(allProductDtos, "탄산수", 1200, 0, "null");
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

    @Test
    @DisplayName("재고가 부족할 때 예외가 발생하는지 확인한다")
    void checkStock_ShouldThrowException_WhenRequestedQuantityExceedsStock() {
        // CSV에서 로드된 데이터를 사용하여 테스트
        Map<String, Integer> shoppingCart = Map.of("콜라", 21);  // 요청 수량이 재고보다 많음

        assertThatThrownBy(() -> storeService.checkStock(shoppingCart))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(ErrorMessage.EXCEEDS_STOCK.getMessage());
    }

    @Test
    @DisplayName("장바구니 항목이 올바르게 처리되는지 확인한다")
    void checkShoppingCart_ShouldReturnCorrectShoppingCartChecks() {
        Map<String, Integer> shoppingCart = Map.of("사이다", 2);  // 2개 요청
        List<ShoppingCartCheck> cartChecks = storeService.checkShoppingCart(shoppingCart);

        assertThat(cartChecks).hasSize(1);
        ShoppingCartCheck check = cartChecks.getFirst();
        assertThat(check.getProductName()).isEqualTo("사이다");
        assertThat(check.getRequestCount()).isEqualTo(2);
        assertThat(check.getPromotionCount()).isEqualTo(2);  // 프로모션 적용 여부 확인
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
}