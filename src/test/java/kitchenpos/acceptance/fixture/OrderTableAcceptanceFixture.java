package kitchenpos.acceptance.fixture;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static kitchenpos.acceptance.fixture.AcceptanceFixture.*;

public class OrderTableAcceptanceFixture {
    private static final String ENDPOINT = "/api/order-tables";

    public static ExtractableResponse<Response> createOrderTable() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "1번");

        return post(params, ENDPOINT);
    }

    public static ExtractableResponse<Response> findAll() {
        return get(ENDPOINT);
    }

    public static ExtractableResponse<Response> sit(String orderTableId) {
        return put(ENDPOINT + "/" + orderTableId + "/sit");
    }

    public static ExtractableResponse<Response> changeNumberOfGuests(String orderTableId) {
        Map<String, Object> guestsParams = new HashMap<>();
        guestsParams.put("numberOfGuests", 4);
        return put(guestsParams, ENDPOINT + "/" + orderTableId + "/number-of-guests");
    }

    public static ExtractableResponse<Response> clear(String orderTableId) {
        return put(ENDPOINT + "/" + orderTableId + "/clear");
    }
}
