package kitchenpos.acceptance;

import java.util.Map;

public enum MenuGroupData {
    추천_메뉴(Map.of("name", "추천 메뉴")),
    금주의_할인_메뉴(Map.of("name", "금주의 할인 메뉴"));

    private final Map<String, Object> value;

    MenuGroupData(Map<String, Object> value) {
        this.value = value;
    }

    public Map<String, Object> getValue() {
        return value;
    }
}
