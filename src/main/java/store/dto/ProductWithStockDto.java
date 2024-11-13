package store.dto;

public record ProductWithStockDto(
        String name,
        int price,
        int stock,
        String promotion
) {
}
