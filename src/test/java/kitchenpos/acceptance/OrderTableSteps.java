package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.UUID;

public class OrderTableSteps {

    public static ExtractableResponse<Response> 테이블_등록_요청(final RequestSpecification given, final Map<String, Object> params) {
        return given.body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/order-tables")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static ExtractableResponse<Response> 테이블_목록_조회_요청(final RequestSpecification given) {
        return given
                .when().get("/api/order-tables")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 테이블에_손님이_앉음을_요청(final RequestSpecification given, final UUID id) {
        return given
                .when().put("/api/order-tables/{id}/sit", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }


    public static ExtractableResponse<Response> 테이블에_앉은_손님인원을_수정_요청(final RequestSpecification given, final UUID id, final Map<String, Object> params) {
        return given.body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/order-tables/{id}/number-of-guests", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }


    public static ExtractableResponse<Response> 손님이_테이블에서_일어남을_요청(final RequestSpecification given, final UUID id) {
        return given
                .when().put("/api/order-tables/{id}/clear", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
