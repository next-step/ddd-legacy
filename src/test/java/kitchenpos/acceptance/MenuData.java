package kitchenpos.acceptance;

import java.util.List;
import java.util.Map;

public class MenuData {
    public static Map<String, Object> 강정치킨_2마리(String menuGroupId, String productId) {
        return Map.of(
                "name", "강정치킨 2마리",
                "price", 19000,
                "menuGroupId", menuGroupId,
                "displayed", true,
                "menuProducts", List.of(
                        Map.of("productId", productId,
                                "quantity", 2)
                ));
    }
}
