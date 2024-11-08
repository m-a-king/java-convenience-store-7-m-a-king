package store.model.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.model.domain.Promotion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PromotionRepositoryTest {

    @Test
    @DisplayName("마크다운 파일의 라인 수와 로드된 프로모션 개수가 일치하는지 검증한다.")
    void loadPromotionsFromMarkdownFile() throws IOException {
        // given
        String filePath = "src/main/resources/promotions.md";
        PromotionRepository promotionRepository = new PromotionRepository();
        promotionRepository.loadPromotions(filePath);

        // 헤더 제외
        int expectedLineCount = (int) Files.lines(Paths.get(filePath)).skip(1).count();

        // when
        List<Promotion> actualPromotions = promotionRepository.findAll();

        // then
        assertThat(actualPromotions.size())
                .isEqualTo(expectedLineCount); // 로드한 프로모션의 수가 파일의 라인 수와 같은지 확인
    }

    @Test
    @DisplayName("이름으로 프로모션을 정확히 찾을 수 있다.")
    void findByName() {
        // given
        String filePath = "src/main/resources/promotions.md";
        PromotionRepository promotionRepository = new PromotionRepository();
        promotionRepository.loadPromotions(filePath);

        // 특정 프로모션 설정 (예: "탄산2+1" 프로모션)
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
}