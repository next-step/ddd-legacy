package kitchenpos.ui.requestor;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Order;
import org.springframework.http.MediaType;

import java.util.UUID;

public class OrderRequestor {

    private static final String DEFAULT_URL = "/api/orders";

    public static ExtractableResponse<Response> 주문생성요청(Order order) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(order)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract();
    }

    public static UUID 주문생성요청_주문식별번호반환(Order order) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(order)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract().jsonPath().getObject("id", UUID.class);
    }

    public static ExtractableResponse<Response> 주문수락요청(UUID orderId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("orderId", orderId)
                .when().put(DEFAULT_URL + "/{orderId}/accept")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문제공요청(UUID orderId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("orderId", orderId)
                .when().put(DEFAULT_URL + "/{orderId}/serve")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문배달시작요청(UUID orderId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("orderId", orderId)
                .when().put(DEFAULT_URL + "/{orderId}/start-delivery")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문배달완료요청(UUID orderId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("orderId", orderId)
                .when().put(DEFAULT_URL + "/{orderId}/complete-delivery")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문완료요청(UUID orderId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("orderId", orderId)
                .when().put(DEFAULT_URL + "/{orderId}/complete")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문전체조회요청() {
        return RestAssured.given().log().all()
                .when().get(DEFAULT_URL)
                .then().log().all()
                .extract();
    }
    
}
