package store.view;

import store.dto.ProductWithStockDto;

import java.util.List;

public class OutputView {

    public void printHelloMessage() {
        System.out.println("안녕하세요. W편의점입니다.");
    }

    public void printProducts(List<ProductWithStockDto> products) {
        System.out.println("현재 보유하고 있는 상품입니다.\n");
        products.forEach(product -> {
            System.out.printf("- %s %,d원 %,d개 %s%n", product.name(), product.price(), product.stock(), product.promotion());
        });
    }

    public void printPurchasePrompt() {
        System.out.println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
    }

    public void printMembershipPrompt() {
        System.out.println("멤버십 할인을 받으시겠습니까? (Y/N)");
    }

    public void printAdditionalPurchasePrompt() {
        System.out.println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
    }
}

