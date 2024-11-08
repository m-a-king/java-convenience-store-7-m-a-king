package store.model.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.model.domain.Product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ProductRepositoryTest {

    @Test
    @DisplayName("마크다운 파일의 라인 수와 로드된 제품 개수가 일치하는지 검증한다.")
    void loadProductsFromMarkdownFile() throws IOException {
        // given
        String filePath = "src/main/resources/products.md";
        ProductRepository productRepository = new ProductRepository();
        productRepository.loadProducts(filePath);

        // 헤더 제외
        int expectedLineCount = (int) Files.lines(Paths.get(filePath)).skip(1).count();

        // when
        List<Product> actualProducts = productRepository.findAll();

        // then
        assertThat(actualProducts.size())
                .isEqualTo(expectedLineCount); // 로드한 제품의 수가 파일의 라인 수와 같은지 확인
    }

    @Test
    @DisplayName("특정 타입과 이름으로 제품을 정확히 찾을 수 있다.")
    void findByTypeAndName() throws IOException {
        // given
        String filePath = "src/main/resources/products.md";
        ProductRepository productRepository = new ProductRepository();
        productRepository.loadProducts(filePath);

        String type = "promotion";
        String name = "콜라";

        Product expectedProduct = new Product(name, 1000, "탄산2+1");

        // when
        Product actualProduct = productRepository.findByTypeAndName(type, name);

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
        ProductRepository productRepository = new ProductRepository();

        // then
        assertThatThrownBy(() -> productRepository.loadProducts(invalidFilePath))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("No such file");
    }

    @Test
    @DisplayName("파일을 로드하지 않은 상태에서 조회하면 빈 리스트를 반환한다.")
    void findAllWithoutLoadingFile() {
        // given
        ProductRepository productRepository = new ProductRepository();

        // when
        List<Product> actualProducts = productRepository.findAll();

        // then
        assertThat(actualProducts)
                .isEmpty();
    }

}