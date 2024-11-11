package store.model.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import store.constant.ProductType;
import store.model.domain.Product;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ProductRepositoryTest {

    private ProductRepository productRepository;

    @Test
    @DisplayName("특정 타입과 이름으로 제품을 정확히 찾을 수 있다.")
    void findInfo() {
        // given
        productRepository = new ProductRepository();

        String name = "콜라";

        Product expectedProduct = new Product(name, 1000, "탄산2+1");

        // when
        Product actualProduct = productRepository.findInfo(name, ProductType.PROMOTION);

        // then
        assertThat(actualProduct)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedProduct);
    }

    @Test
    @DisplayName("잘못된 파일 경로로 로드 시 예외가 발생한다.")
    void loadProductsWithInvalidFilePath() {
        // given
        String invalidFilePath = "src/main/resources/nonexistent.md";

        // then
        assertThatThrownBy(() -> new ProductRepository(invalidFilePath))
                .hasMessageContaining("잘못된 데이터 파일입니다");
    }

    @Test
    @DisplayName("파일을 로드하지 않은 상태에서 조회할 수 없다")
    void findAllInfoWithoutLoadingFile() {
        // when
        // then
        assertThatThrownBy(() -> productRepository.findAllInfo())
                .isInstanceOf(NullPointerException.class);
    }


    @Test
    @DisplayName("getStock 메서드가 제품의 정확한 재고 수량을 반환한다.")
    void findStock_ShouldReturnCorrectStockByTypeAndName()  {
        // given
        productRepository = new ProductRepository();

        String name = "콜라";

        // when
        int stock = productRepository.findStock(name, ProductType.REGULAR);

        // then
        assertThat(stock).isEqualTo(10);
    }

    @Test
    @DisplayName("reduceStock 메서드가 재고를 올바르게 감소시킨다.")
    void reduceStock_ShouldDecreaseStockCorrectly() {
        // given
        productRepository = new ProductRepository();

        String name = "콜라";

        int initialStock = productRepository.findStock(name, ProductType.PROMOTION);
        int quantityToReduce = 5;

        // when
        boolean result = productRepository.reduceStock(name, ProductType.PROMOTION, quantityToReduce);

        // then
        assertThat(result).isTrue();
        assertThat(productRepository.findStock(name, ProductType.PROMOTION)).isEqualTo(initialStock - quantityToReduce);
    }

    @Test
    @DisplayName("reduceStock 메서드가 재고 부족 시 감소를 거부한다.")
    void reduceStock_ShouldFailWhenInsufficientStock() {
        // given
        productRepository = new ProductRepository();

        String name = "콜라";

        int initialStock = productRepository.findStock(name, ProductType.PROMOTION);
        int quantityToReduce = initialStock + 1;

        // when
        boolean result = productRepository.reduceStock(name, ProductType.PROMOTION, quantityToReduce);

        // then
        assertThat(result).isFalse();
        assertThat(productRepository.findStock(name, ProductType.PROMOTION)).isEqualTo(initialStock);
    }

    @ParameterizedTest
    @DisplayName("재고를 연속으로 두 번 감소시키고 남은 개수를 확인한다.")
    @MethodSource("provideProductReductionParameters")
    void reduceStock_ShouldHandleConsecutiveReductions(ProductType productType, String name, int firstReduce, int secondReduce, boolean expectedResult) {
        // given
        productRepository = new ProductRepository();
        int initialStock = productRepository.findStock(name, productType);

        // when
        productRepository.reduceStock(name, productType, firstReduce);
        int stockAfterFirstReduction = productRepository.findStock(name, productType);

        boolean secondResult = productRepository.reduceStock(name, productType, secondReduce);
        int finalStock = productRepository.findStock(name, productType);

        // then
        assertThat(stockAfterFirstReduction).isEqualTo(initialStock - firstReduce);
        assertThat(secondResult).isEqualTo(expectedResult);
        if (expectedResult) {
            assertThat(finalStock).isEqualTo(stockAfterFirstReduction - secondReduce);
        } else {
            assertThat(finalStock).isEqualTo(stockAfterFirstReduction);
        }
    }

    private static Stream<Arguments> provideProductReductionParameters() {
        return Stream.of(
                Arguments.of(ProductType.PROMOTION, "콜라", 3, 4, true),   // 충분한 재고로 두 번 감소
                Arguments.of(ProductType.PROMOTION, "콜라", 5, 6, false),  // 두 번째 감소 시 재고 부족
                Arguments.of(ProductType.REGULAR, "사이다", 2, 3, true),   // 다른 상품 및 타입의 테스트
                Arguments.of(ProductType.REGULAR, "사이다", 3, 8, false)   // 두 번째 감소 시 재고 부족
        );
    }

}