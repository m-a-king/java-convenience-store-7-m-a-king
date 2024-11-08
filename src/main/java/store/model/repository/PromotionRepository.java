package store.model.repository;

import store.model.domain.Promotion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PromotionRepository {
    private final Map<String, Promotion> promotions = new LinkedHashMap<>();

    public void loadPromotions(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // 헤더 생략

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String name = data[0];
                int buy = Integer.parseInt(data[1]);
                int get = Integer.parseInt(data[2]);
                LocalDate startDate = LocalDate.parse(data[3]);
                LocalDate endDate = LocalDate.parse(data[4]);

                Promotion promotion = new Promotion(name, buy, get, startDate, endDate);
                promotions.put(name, promotion);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Promotion findByName(String name) {
        return promotions.getOrDefault(name, null);
    }

    public List<Promotion> findAll() {
        return new ArrayList<>(promotions.values());
    }
}