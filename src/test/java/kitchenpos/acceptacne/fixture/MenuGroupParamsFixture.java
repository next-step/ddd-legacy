package kitchenpos.acceptacne.fixture;

import java.util.HashMap;
import java.util.Map;

public class MenuGroupParamsFixture {
    public static Map<String, Object> createMenuGroupParams(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        return params;
    }
}
