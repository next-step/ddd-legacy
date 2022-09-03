package kitchenpos.order;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Order;
import org.springframework.http.MediaType;

import static kitchenpos.AcceptanceTestSteps.given;

public class OrderSteps {
    public static ExtractableResponse<Response> 주문_생성_요청(Order order) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(order)
                .when().post("/api/orders")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_수락_요청(String path) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + "/accept")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_서빙_요청(String path) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + "/serve")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 배달_시작_요청(String path) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + "/start-delivery")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 배달_완료_요청(String path) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + "/complete-delivery")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_처리_완료_요청(String path) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(path + "/complete")
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문목록_조회_요청() {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/api/orders")
                .then().log().all().extract();
    }
}
