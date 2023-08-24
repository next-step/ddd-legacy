package kitchenpos.acceptance;

import java.util.Map;

public enum ProductData {
    강정치킨(Map.of(
            "name", "강정치킨",
            "price", 18000)),
    양념치킨(Map.of(
            "name", "양념치킨",
            "price", 17000));

    private final Map<String, Object> value;

    ProductData(Map<String, Object> value) {
        this.value = value;
    }

    public Map<String, Object> getValue() {
        return value;
    }
}
