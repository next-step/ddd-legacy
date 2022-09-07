package kitchenpos.ordertable;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static kitchenpos.AcceptanceTestSteps.given;

public class OrderTableSteps {
    public static ExtractableResponse<Response> 주문테이블_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/api/order-tables")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문테이블에_앉기_요청(String path) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + "/sit")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문테이블_치우기_요청(String path) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + "/clear")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문테이블_인원수_변경_요청(String path, int numberOfGuests) {
        Map<String, String> params = new HashMap<>();
        params.put("numberOfGuests", numberOfGuests + "");

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(path + "/number-of-guests")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문테이블_목록_조회_요청() {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/order-tables")
                .then().log().all().extract();
    }
}
