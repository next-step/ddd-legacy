package kitchenpos.ui.fixture;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static kitchenpos.ui.fixture.AcceptanceSupport.*;

public class ProductAcceptanceFixture {
    private static final String ENDPOINT = "/api/products";

    public static ExtractableResponse<Response> createProduct() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "강정치킨");
        params.put("price", "17000");

        return post(params, ENDPOINT);
    }

    public static ExtractableResponse<Response> changePrice(String productId) {
        Map<String, Object> params = new HashMap<>();
        params.put("price", "18000");

        return put(params, ENDPOINT + "/" + productId + "/price");
    }

    public static ExtractableResponse<Response> findAll() {
        return get(ENDPOINT);
    }
}
