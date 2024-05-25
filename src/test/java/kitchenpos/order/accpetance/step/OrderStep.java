package kitchenpos.order.accpetance.step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Order;
import kitchenpos.support.RestAssuredClient;

import java.util.UUID;

public class OrderStep {

    private static final String ORDER_BASE_URL = "/api/orders";

    public static ExtractableResponse<Response> 주문을_등록한다(Order request) {
        return RestAssuredClient.post(ORDER_BASE_URL, request);
    }

    public static ExtractableResponse<Response> 주문_목록을_조회한다() {
        return RestAssuredClient.get(ORDER_BASE_URL);
    }

    public static ExtractableResponse<Response> 주문을_수락한다(UUID request) {
        var url = String.format("%s/%s/accept", ORDER_BASE_URL, request);
        return RestAssuredClient.put(url);
    }

    public static ExtractableResponse<Response> 음식을_전달한다(UUID request) {
        var url = String.format("%s/%s/serve", ORDER_BASE_URL, request);
        return RestAssuredClient.put(url);
    }

    public static ExtractableResponse<Response> 배달을_시작한다(UUID request) {
        var url = String.format("%s/%s/start-delivery", ORDER_BASE_URL, request);
        return RestAssuredClient.put(url);
    }

    public static ExtractableResponse<Response> 배달을_완료한다(UUID request) {
        var url = String.format("%s/%s/complete-delivery", ORDER_BASE_URL, request);
        return RestAssuredClient.put(url);
    }

    public static ExtractableResponse<Response> 주문을_완료한다(UUID request) {
        var url = String.format("%s/%s/complete", ORDER_BASE_URL, request);
        return RestAssuredClient.put(url);
    }

}
