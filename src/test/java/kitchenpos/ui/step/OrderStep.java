package kitchenpos.ui.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Order;
import org.springframework.http.MediaType;

import java.util.UUID;

public class OrderStep {

    private static final String PATH = "/api/orders";

    public static ExtractableResponse<Response> 주문_생성_요청(Order param) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(param)
                .when()
                .post(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_접수_요청(UUID id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(PATH + "/{orderId}/accept", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_서빙_요청(UUID id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(PATH + "/{orderId}/serve", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_완료_요청(UUID id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(PATH + "/{orderId}/complete", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_배달_시작_요청(UUID id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(PATH + "/{orderId}/start-delivery", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_배달_완료_요청(UUID id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(PATH + "/{orderId}/complete-delivery", id)
                .then().log().all()
                .extract();
    }

}
