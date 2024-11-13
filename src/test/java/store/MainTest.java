package store;

import camp.nextstep.edu.missionutils.test.NsTest;
import org.junit.jupiter.api.Test;

import static camp.nextstep.edu.missionutils.test.Assertions.assertSimpleTest;
import static org.assertj.core.api.Assertions.assertThat;

class MainTest extends NsTest {

    @Test
    void 전체_구매_시나리오_테스트() {
        assertSimpleTest(() -> {
            run(
                    // 첫 번째 구매 시도
                    "[콜라-3],[에너지바-5]", // 상품 선택
                    "Y", // 멤버십 할인 여부
                    "Y", // 추가 구매

                    // 두 번째 구매 시도
                    "[콜라-10]", // 상품 선택
                    "Y", // 프로모션 할인이 적용되지 않는 상품 구매 여부
                    "N", // 멤버십 할인 거부
                    "Y", // 추가 구매

                    // 세 번째 구매 시도
                    "[오렌지주스-1]", // 상품 선택
                    "Y", // 무료 추가 상품 수령 여부
                    "Y", // 멤버십 할인 여부
                    "Y",  // 추가 구매

                    // No line found 방지
                    "[오렌지주스-1]", // 상품 선택
                    "Y", // 무료 추가 상품 수령 여부
                    "Y", // 멤버십 할인 여부
                    "N"  // 추가 구매 거부
            );

            // 첫 번째 구매 결과 검증 및 남은 재고 확인
            assertThat(output().replaceAll("\\s", "")).contains(
                    "==============W편의점================",
                    "상품명수량금액",
                    "콜라33,000",
                    "에너지바510,000",
                    "=============증정==============",
                    "콜라1",
                    "====================================",
                    "총구매액813,000",
                    "행사할인-1,000",
                    "멤버십할인-3,000",
                    "내실돈9,000"
            );

            // 첫 번째 구매 후 재고 확인
            assertThat(output().replaceAll("\\s", "")).contains(
                    "-콜라1,000원7개탄산2+1", // 콜라 재고가 10개에서 7개로 감소
                    "-에너지바2,000원재고없음", // 에너지바 재고가 5개에서 0개로 감소
                    "-오렌지주스1,800원재고없음",
                    "-탄산수1,200원재고없음"
            );

            // 두 번째 구매 결과 검증 및 남은 재고 확인
            assertThat(output().replaceAll("\\s", "")).contains(
                    "==============W편의점================",
                    "상품명수량금액",
                    "콜라1010,000",
                    "=============증정==============",
                    "콜라2",
                    "====================================",
                    "총구매액1010,000",
                    "행사할인-2,000",
                    "멤버십할인-0",
                    "내실돈8,000"
            );

            // 두 번째 구매 후 재고 확인
            assertThat(output().replaceAll("\\s", "")).contains(
                    "-콜라1,000원재고없음탄산2+1"
            );

            // 세 번째 구매 결과 검증 및 남은 재고 확인
            assertThat(output().replaceAll("\\s", "")).contains(
                    "==============W편의점================",
                    "상품명수량금액",
                    "오렌지주스23,600",
                    "=============증정==============",
                    "오렌지주스1",
                    "====================================",
                    "총구매액23,600",
                    "행사할인-1,800",
                    "멤버십할인-0",
                    "내실돈1,800"
            );

            // 세 번째 구매 후 재고 확인
            assertThat(output().replaceAll("\\s", "")).contains(
                    "-오렌지주스1,800원7개MD추천상품"
            );
        });
    }

    @Override
    public void runMain() {
        Application.main(new String[]{});
    }
}