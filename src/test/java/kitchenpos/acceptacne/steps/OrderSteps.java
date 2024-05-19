package kitchenpos.acceptacne.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.RestAssuredUtils;
import kitchenpos.domain.Order;

import java.util.UUID;

public class OrderSteps {

    private static final String URI = "/api/orders";

    public static ExtractableResponse<Response> 주문을_등록한다(Order request) {
        return RestAssuredUtils.post(request, URI);
    }

    public static ExtractableResponse<Response> 주문을_수락한다(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/accept", id));
    }

    public static ExtractableResponse<Response> 주문의_제조를_완료한다(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/serve", id));
    }

    public static ExtractableResponse<Response> 배달을_시작한다(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/start-delivery", id));
    }

    public static ExtractableResponse<Response> 배달을_완료한다(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/complete-delivery", id));
    }

    public static ExtractableResponse<Response> 주문을_완료한다(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/complete", id));
    }
}
