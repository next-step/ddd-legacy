package kitchenpos.ordertable.acceptance.step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.OrderTable;
import kitchenpos.support.RestAssuredClient;

import java.util.UUID;

public class OrderTableStep {

    private static final String ORDER_TABLE_BASE_URL = "/api/order-tables";

    public static ExtractableResponse<Response> 테이블을_등록한다(OrderTable request) {
        return RestAssuredClient.post(ORDER_TABLE_BASE_URL, request);
    }

    public static ExtractableResponse<Response> 테이블_목록을_조회한다() {
        return RestAssuredClient.get(ORDER_TABLE_BASE_URL);
    }

    public static ExtractableResponse<Response> 테이블에_앉다(UUID id) {
        var url = String.format("%s/%s/sit", ORDER_TABLE_BASE_URL, id);
        return RestAssuredClient.put(url);
    }

    public static ExtractableResponse<Response> 테이블을_정리하다(UUID id) {
        var url = String.format("%s/%s/clear", ORDER_TABLE_BASE_URL, id);
        return RestAssuredClient.put(url);
    }

}
