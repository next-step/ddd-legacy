package kitchenpos.acceptacne.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.RestAssuredUtils;
import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableSteps {

    private static final String URI = "/api/order-tables";

    public static ExtractableResponse<Response> createOrderTableStep(OrderTable orderTable) {
        return RestAssuredUtils.post(orderTable, URI);
    }

    public static ExtractableResponse<Response> sitOrderTableStep(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/sit", id));
    }

    public static ExtractableResponse<Response> clearOrderTableStep(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/clear", id));
    }

    public static ExtractableResponse<Response> changeNumberOfGuestsOrderTableStep(UUID id, OrderTable orderTable) {
        return RestAssuredUtils.put(orderTable, String.format(URI + "/%s/number-of-guests", id));
    }

    public static ExtractableResponse<Response> getOrderTablesStep() {
        return RestAssuredUtils.get(URI);
    }
}
