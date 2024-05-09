package kitchenpos.acceptacne.steps;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.RestAssuredUtils;
import kitchenpos.domain.Order;

import java.util.UUID;

public class OrderSteps {

    private static final String URI = "/api/orders";

    public static ExtractableResponse<Response> createOrderStep(Order request) {
        return RestAssuredUtils.post(request, URI);
    }

    public static ExtractableResponse<Response> acceptOrderStep(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/accept", id));
    }

    public static ExtractableResponse<Response> serveOrderStep(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/serve", id));
    }

    public static ExtractableResponse<Response> startDeliveryOrderStep(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/start-delivery", id));
    }

    public static ExtractableResponse<Response> completeDeliveryOrderStep(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/complete-delivery", id));
    }

    public static ExtractableResponse<Response> completeOrderStep(UUID id) {
        return RestAssuredUtils.put(String.format(URI + "/%s/complete", id));
    }
}
