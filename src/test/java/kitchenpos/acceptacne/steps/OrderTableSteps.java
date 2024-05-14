package kitchenpos.acceptacne.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.RestAssuredUtils;
import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableSteps {

    private static final String URI = "/api/order-tables";

    public static ExtractableResponse<Response> 주문테이블을_등록한다(OrderTable orderTable) {
        return RestAssuredUtils.post(orderTable, URI);
    }

    public static ExtractableResponse<Response> 주문테이블에_앉는다(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/sit", id));
    }

    public static ExtractableResponse<Response> 주문테이블을_초기화한다(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/clear", id));
    }

    public static ExtractableResponse<Response> 주문테이블의_인원을_변경한다(UUID id, OrderTable orderTable) {
        return RestAssuredUtils.put(orderTable, String.format(URI + "/%s/number-of-guests", id));
    }

    public static ExtractableResponse<Response> getOrderTablesStep() {
        return RestAssuredUtils.get(URI);
    }
}
