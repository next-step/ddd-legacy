package kitchenpos.acceptance.fixture;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static kitchenpos.acceptance.fixture.AcceptanceFixture.*;

public class MenuGroupAcceptanceFixture {
    private static final String ENDPOINT = "/api/menu-groups";

    public static ExtractableResponse<Response> createMenu() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "best 메뉴");

        return post(params, ENDPOINT);
    }

    public static ExtractableResponse<Response> findAll() {
        return get(ENDPOINT);
    }
}
