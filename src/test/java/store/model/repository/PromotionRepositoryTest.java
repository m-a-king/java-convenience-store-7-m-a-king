package store.model.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.model.domain.Promotion;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PromotionRepositoryTest {

    private PromotionRepository promotionRepository;

    @Test
    @DisplayName("이름으로 프로모션을 정확히 찾을 수 있다.")
    void findByName() throws IOException {
        // given
        promotionRepository = new PromotionRepository();

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