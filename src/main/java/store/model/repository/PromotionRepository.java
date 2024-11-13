package store.model.repository;

import store.Message.ErrorMessage;
import store.config.DataInitializer;
import store.model.domain.Promotion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PromotionRepository {
    public static final String DEFAULT_PROMOTIONS_FILE_PATH = "src/main/resources/promotions.md";
    private final Map<String, Promotion> promotions = new LinkedHashMap<>();

    public PromotionRepository(String filePath) {
        DataInitializer initializer = DataInitializer.getInstance();
        try {
            Map<String, Promotion> promotionData = initializer.loadPromotions(filePath);
            this.promotions.putAll(promotionData);
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessage.FILE_ERROR.getMessage(filePath));
        }
    }

    public PromotionRepository() {
        this(DEFAULT_PROMOTIONS_FILE_PATH);
    }

    public Promotion findByName(String name) {
        return promotions.getOrDefault(name, null);
    }

    public List<Promotion> findAll() {
        return new ArrayList<>(promotions.values());
    }
}