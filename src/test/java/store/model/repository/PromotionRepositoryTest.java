package store.model.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.model.domain.Promotion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PromotionRepositoryTest {

    public static final String TEST_PRODUCT_FILE_PATH = "src/main/resources/testpromotions.md";
    private PromotionRepository promotionRepository;

    @Test
    @DisplayName("마크다운 파일의 라인 수와 로드된 프로모션 개수가 일치하는지 검증한다.")
    void loadPromotionsFromMarkdownFile() throws IOException {
        // given
        promotionRepository = new PromotionRepository(TEST_PRODUCT_FILE_PATH);

        // 헤더 제외
        int expectedLineCount = (int) Files.lines(Paths.get(TEST_PRODUCT_FILE_PATH)).skip(1).count();

        // when
        List<Promotion> actualPromotions = promotionRepository.findAll();

        // then
        assertThat(actualPromotions.size())
                .isEqualTo(expectedLineCount); // 로드한 프로모션의 수가 파일의 라인 수와 같은지 확인
    }

    @Test
    @DisplayName("이름으로 프로모션을 정확히 찾을 수 있다.")
    void findByName() throws IOException {
        // given
        promotionRepository = new PromotionRepository(TEST_PRODUCT_FILE_PATH);

        String name = "탄산2+1";
        Promotion expectedPromotion = new Promotion(name, 2, 1, LocalDate.parse("2024-01-01"), LocalDate.parse("2024-12-31"));

        // when
        Promotion actualPromotion = promotionRepository.findByName(name);

        // then
        assertThat(actualPromotion)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedPromotion);
    }

    @Test
    @DisplayName("잘못된 파일 경로로 로드 시 예외가 발생한다.")
    void loadProductsWithInvalidFilePath() {
        // given
        String invalidFilePath = "src/main/resources/nonexistent.md";

        // then
        assertThatThrownBy(() -> new PromotionRepository(invalidFilePath))
                .hasMessageContaining("잘못된 데이터 파일입니다");
    }

    @Test
    @DisplayName("파일을 로드하지 않은 상태에서 조회하면 빈 리스트를 반환한다.")
    void findAllWithoutLoadingFile() {
        // when
        // then
        assertThatThrownBy(() -> promotionRepository.findAll())
                .isInstanceOf(NullPointerException.class);
    }
}