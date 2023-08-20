package kitchenpos.acceptance.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderSteps {

    private static final String URI = "/api/orders";

    public static ExtractableResponse<Response> 포장_주문_생성(List<OrderLineItem> orderLineItems) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", OrderType.TAKEOUT);
        params.put("orderLineItems", orderLineItems);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post(URI)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 배달_주문_생성(String deliveryAddress, List<OrderLineItem> orderLineItems) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", OrderType.DELIVERY);
        params.put("deliveryAddress", deliveryAddress);
        params.put("orderLineItems", orderLineItems);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post(URI)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 매장_주문_생성(UUID orderTableId, List<OrderLineItem> orderLineItems) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", OrderType.EAT_IN);
        params.put("orderTableId", orderTableId);
        params.put("orderLineItems", orderLineItems);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post(URI)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_접수(UUID orderId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URI + "/{orderId}/accept", orderId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_서빙(UUID orderId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URI + "/{orderId}/serve", orderId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_배달_요청(UUID orderId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URI + "/{orderId}/start-delivery", orderId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_배달_완료(UUID orderId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URI + "/{orderId}/complete-delivery", orderId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_완료(UUID orderId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URI + "/{orderId}/complete", orderId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_전체_조회() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(URI)
                .then().log().all().extract();
    }
}
