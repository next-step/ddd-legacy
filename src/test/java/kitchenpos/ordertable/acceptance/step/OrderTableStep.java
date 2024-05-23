package kitchenpos.ordertable.acceptance.step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.OrderTable;
import kitchenpos.support.RestAssuredClient;

public class OrderTableStep {

    private static final String ORDER_TABLE_BASE_URL = "/api/order-tables";

    public static ExtractableResponse<Response> 테이블을_등록한다(OrderTable request) {
        return RestAssuredClient.post(ORDER_TABLE_BASE_URL, request);
    }

    public static ExtractableResponse<Response> 테이블_목록을_조회한다() {
        return RestAssuredClient.get(ORDER_TABLE_BASE_URL);
    }

}
