package store.view;

import camp.nextstep.edu.missionutils.Console;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

public class InputViewTest {
    private InputView inputView;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        inputView = new InputView();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        Console.close();
        System.setOut(System.out); // 출력 스트림 복원
    }

    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    @DisplayName("구매 요청 메시지를 출력한다")
    void printPurchasePrompt() {
        // given
        setInput("사이다-2,감자칩-1");

        // when
        inputView.readItem();

        // then
        assertThat(outContent.toString()).isEqualTo(
                "\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])\n"
        );
    }

    @Test
    @DisplayName("멤버십 할인 요청 메시지를 출력한다")
    void printMembershipPrompt() {
        // given
        setInput("Y");

        // when
        inputView.readMembershipDecision();

        // then
        assertThat(outContent.toString()).isEqualTo(
                "\n멤버십 할인을 받으시겠습니까? (Y/N)\n"
        );
    }

    @Test
    @DisplayName("추가 구매 요청 메시지를 출력한다")
    void printAdditionalPurchasePrompt() {
        // given
        setInput("N");

        // when
        inputView.readAdditionalPurchaseDecision();

        // then
        assertThat(outContent.toString()).isEqualTo(
                "\n감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)\n"
        );
    }
}