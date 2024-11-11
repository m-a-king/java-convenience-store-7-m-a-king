package store.view;

import camp.nextstep.edu.missionutils.Console;
import store.constant.InputMessages;

public class InputView {

    public String readItem() {
        System.out.println(InputMessages.PURCHASE_PROMPT_MESSAGE);
        return Console.readLine();
    }

    public String readPromotionDecision(String productName) {
        System.out.printf(InputMessages.ADDITIONAL_FREE_PROMPT_MESSAGE, productName);
        return Console.readLine();
    }

    public String readFullPriceDecision(String productName, int fullPriceCount) {
        System.out.printf(InputMessages.REGULAR_PRICE_PROMPT_MESSAGE, productName, fullPriceCount);
        return Console.readLine();
    }

    public String readMembershipDecision() {
        System.out.println(InputMessages.MEMBERSHIP_PROMPT_MESSAGE);
        return Console.readLine();
    }

    public String readAdditionalPurchaseDecision() {
        System.out.println(InputMessages.ADDITIONAL_PURCHASE_PROMPT_MESSAGE);
        return Console.readLine();
    }
}