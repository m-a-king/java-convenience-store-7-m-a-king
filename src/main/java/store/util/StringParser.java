package store.util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class StringParser {

    private StringParser() {
    }

    public static Map<String, Integer> parseToMap(String input) {
        Map<String, Integer> result = new HashMap<>();

        Stream.of(input.split(","))
                .forEach(item -> {
                    item = item.replaceAll("[\\[\\]]", "");
                    String[] nameAndQuantity = item.split("-");
                    if (nameAndQuantity.length != 2) {
                        throw new IllegalArgumentException("상품명과 수량을 정확히 입력해 주세요.");
                    }
                    result.put(nameAndQuantity[0], Integer.valueOf(nameAndQuantity[1]));
                });

        return result;
    }
}