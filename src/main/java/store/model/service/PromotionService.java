package store.model.service;

import store.model.domain.Promotion;
import store.model.repository.PromotionRepository;

public class PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public Promotion getPromotion(String name) {
        return promotionRepository.findByName(name);
    }
}
