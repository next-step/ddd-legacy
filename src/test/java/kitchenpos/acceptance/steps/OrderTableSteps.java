package kitchenpos.acceptance.steps;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderTableSteps {

    private static final String URI = "/api/order-tables";

    public static ExtractableResponse<Response> 주문테이블을_생성한다(String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post(URI)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문테이블을_사용한다(UUID orderTableId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URI + "/{orderTableId}/sit", orderTableId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문테이블을_치운다(UUID orderTableId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(URI + "/{orderTableId}/clear", orderTableId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 인원수를_바꾼다(UUID orderTableId, int numberOfGuests) {
        Map<String, Object> params = new HashMap<>();
        params.put("numberOfGuests", numberOfGuests);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(URI + "/{orderTableId}/number-of-guests", orderTableId)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문테이블_전체를_조회한다() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(URI)
                .then().log().all().extract();
    }
}
