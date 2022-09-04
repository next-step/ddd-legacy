package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.UUID;

public class OrderSteps {

    public static final String 매장식사 = "EAT_IN";
    public static final String 포장 = "TAKEOUT";
    public static final String 배달 = "DELIVERY";

    public static final String 대기 = "WAITING";
    public static final String 승인 = "ACCEPTED";
    public static final String 서빙완료 = "SERVED";
    public static final String 배송중 = "DELIVERING";
    public static final String 배송완료 = "DELIVERED";
    public static final String 완료 = "COMPLETED";

    public static ExtractableResponse<Response> 주문_등록_요청(final RequestSpecification given, final Map<String, Object> params) {
        return given.body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/orders")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static ExtractableResponse<Response> 주문_목록_조회_요청(final RequestSpecification given) {
        return given
                .when().get("/api/orders")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 주문_승인_요청(final RequestSpecification given, final UUID id) {
        return given
                .when().put("/api/orders/{id}/accept", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 주문_서빙_요청(final RequestSpecification given, final UUID id) {
        return given
                .when().put("/api/orders/{id}/serve", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 주문_배달_요청(final RequestSpecification given, final UUID id) {
        return given
                .when().put("/api/orders/{id}/start-delivery", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 주문_배달완료_요청(final RequestSpecification given, final UUID id) {
        return given
                .when().put("/api/orders/{id}/complete-delivery", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 주문_완료_요청(final RequestSpecification given, final UUID id) {
        return given
                .when().put("/api/orders/{id}/complete", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
