package store.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.dto.ProductWithStockDto;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OutputViewTest {

    private OutputView outputView;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        outputView = new OutputView();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    @DisplayName("환영 메시지를 출력한다")
    void printHelloMessage() {
        // when
        outputView.printHelloMessage();

        // then
        assertThat(outContent.toString()).isEqualTo("안녕하세요. W편의점입니다.\n");
    }

    @Test
    @DisplayName("상품 목록을 올바르게 출력한다")
    void printProducts() {
        // given
        List<ProductWithStockDto> products = List.of(
                new ProductWithStockDto("콜라", 1000, 10, "탄산2+1"),
                new ProductWithStockDto("사이다", 1200, 5, "null")
        );

        // when
        outputView.printProducts(products);

        // then
        assertThat(outContent.toString()).contains(
                "현재 보유하고 있는 상품입니다.\n\n",
                        "- 콜라 1,000원 10개 탄산2+1\n",
                        "- 사이다 1,200원 5개 \n"
        );
    }


}