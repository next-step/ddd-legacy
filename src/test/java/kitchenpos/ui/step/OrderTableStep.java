package kitchenpos.ui.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.OrderTable;
import org.springframework.http.MediaType;

import java.util.UUID;

public class OrderTableStep {

    private final static String PATH = "/api/order-tables";

    public static ExtractableResponse<Response> 주문_테이블_생성_요청(OrderTable param) {
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(PATH)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_테이블_배정_요청(UUID id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(PATH + "/{orderTableId}/sit", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_테이블_손님_인원_변경_요청(UUID id, OrderTable param) {
        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(PATH + "/{orderTableId}/number-of-guests", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 주문_테이블_초기화_요청(UUID id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(PATH + "/{orderTableId}/clear", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 모든_주문_테이블_조회_요청() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(PATH)
                .then().log().all()
                .extract();
    }
}
