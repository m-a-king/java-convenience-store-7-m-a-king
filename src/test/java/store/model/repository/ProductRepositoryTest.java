package store.model.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import store.model.domain.Product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ProductRepositoryTest {

    public static final String TEST_PRODUCT_FILE_PATH = "src/main/resources/testproducts.md";
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new ProductRepository();
    }

    @Test
    @DisplayName("마크다운 파일의 라인 수와 로드된 제품 개수가 일치하는지 검증한다.")
    void loadProductsFromMarkdownFile() throws IOException {
        // given
        productRepository.loadProducts(TEST_PRODUCT_FILE_PATH);

        // 헤더 제외
        int expectedLineCount = (int) Files.lines(Paths.get(TEST_PRODUCT_FILE_PATH)).skip(1).count();

        // when
        List<Product> actualProducts = productRepository.findAllProduct();

        // then
        assertThat(actualProducts.size())
                .isEqualTo(expectedLineCount); // 로드한 제품의 수가 파일의 라인 수와 같은지 확인
    }

    @Test
    @DisplayName("특정 타입과 이름으로 제품을 정확히 찾을 수 있다.")
    void findProductByTypeAndName() throws IOException {
        // given
        productRepository.loadProducts(TEST_PRODUCT_FILE_PATH);

        String type = "promotion";
        String name = "콜라";

        Product expectedProduct = new Product(name, 1000, "탄산2+1");

        // when
        Product actualProduct = productRepository.findProductByTypeAndName(type, name);

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
        assertThatThrownBy(() -> productRepository.loadProducts(invalidFilePath))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("No such file");
    }

    @Test
    @DisplayName("파일을 로드하지 않은 상태에서 조회하면 빈 리스트를 반환한다.")
    void findAllProductWithoutLoadingFile() {
        // when
        List<Product> actualProducts = productRepository.findAllProduct();

        // then
        assertThat(actualProducts)
                .isEmpty();
    }


    @Test
    @DisplayName("getStock 메서드가 제품의 정확한 재고 수량을 반환한다.")
    void findStock_ShouldReturnCorrectStockByTypeAndName() throws IOException {
        // given
        productRepository.loadProducts(TEST_PRODUCT_FILE_PATH);

        String type = "regular";
        String name = "콜라";
        int price = 1000;

        // when
        int stock = productRepository.findStockByProduct(new Product(name, price, type));

        // then
        assertThat(stock).isEqualTo(10);
    }

    @Test
    @DisplayName("reduceStock 메서드가 재고를 올바르게 감소시킨다.")
    void reduceStock_ShouldDecreaseStockCorrectly() throws IOException {
        // given
        productRepository.loadProducts(TEST_PRODUCT_FILE_PATH);

        String name = "콜라";
        int price = 1000;
        String type = "regular";
        Product cola = new Product(name, price, type);

        int initialStock = productRepository.findStockByProduct(cola);
        int quantityToReduce = 5;

        // when
        boolean result = productRepository.reduceStock(cola, quantityToReduce);

        // then
        assertThat(result).isTrue();
        assertThat(productRepository.findStockByProduct(cola)).isEqualTo(initialStock - quantityToReduce);
    }

    @Test
    @DisplayName("reduceStock 메서드가 재고 부족 시 감소를 거부한다.")
    void reduceStock_ShouldFailWhenInsufficientStock() throws IOException {
        // given
        productRepository.loadProducts(TEST_PRODUCT_FILE_PATH);

        String name = "콜라";
        int price = 1000;
        String type = "promotion";
        Product cola = new Product(name, price, type);

        int initialStock = productRepository.findStockByProduct(new Product(name, price, type));
        int quantityToReduce = initialStock + 1;

        // when
        boolean result = productRepository.reduceStock(cola, quantityToReduce);

        // then
        assertThat(result).isFalse(); // 감소 실패를 확인
        assertThat(productRepository.findStockByProduct(new Product(name, price, type))).isEqualTo(initialStock);
    }

    @ParameterizedTest
    @DisplayName("재고를 연속으로 두 번 감소시키고 남은 개수를 확인한다.")
    @CsvSource({
            "promotion, 콜라, 3, 4, 1000, true",  // 충분한 재고로 두 번 감소
            "promotion, 콜라, 5, 6, 1000, false"  // 두 번째 감소 시 재고 부족
    })
    void reduceStock_ShouldHandleConsecutiveReductions(String type, String name, int firstReduce, int secondReduce, int price, boolean expectedResult) throws IOException {
        // given
        productRepository.loadProducts(TEST_PRODUCT_FILE_PATH);
        int initialStock = productRepository.findStockByProduct(new Product(name, price, type));
        Product cola = new Product(name, price, type);

        // when
        productRepository.reduceStock(cola, firstReduce);
        int stockAfterFirstReduction = productRepository.findStockByProduct(new Product(name, price, type));

        boolean secondResult = productRepository.reduceStock(cola, secondReduce);
        int finalStock = productRepository.findStockByProduct(new Product(name, price, type));

        // then
        assertThat(stockAfterFirstReduction).isEqualTo(initialStock - firstReduce);
        assertThat(secondResult).isEqualTo(expectedResult);
        if (expectedResult) {
            assertThat(finalStock).isEqualTo(stockAfterFirstReduction - secondReduce);
            return;
        }
        assertThat(finalStock).isEqualTo(stockAfterFirstReduction);
    }

}