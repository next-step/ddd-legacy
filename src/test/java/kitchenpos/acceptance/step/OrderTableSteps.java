package kitchenpos.acceptance.step;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderTableSteps {
    private static final String ENDPOINT = "/api/order-tables";

    public static ExtractableResponse<Response> 주문_테이블_생성_요청(String name) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createParams(name))
                .when().post(ENDPOINT)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_테이블_착석_상태_변경_요청(ExtractableResponse<Response> createResponse) {
        String id = createResponse.body().jsonPath().getString("id");

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT + "/{orderTableId}/sit", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_테이블_인원수_변경_요청(ExtractableResponse<Response> sitResponse, int numberOfGuests) {
        String id = sitResponse.body().jsonPath().getString("id");
        Map<String, Object> orderTable = sitResponse.body().jsonPath().get(".");
        orderTable.put("numberOfGuests", numberOfGuests + "");

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(orderTable)
                .when().put(ENDPOINT + "/{orderTableId}/number-of-guests", id)
                .then().log().all().extract();
    }

    public static ExtractableResponse<Response> 주문_테이블_비어있음_상태_변경_요청(ExtractableResponse<Response> sitResponse) {
        String id = sitResponse.body().jsonPath().getString("id");

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(ENDPOINT + "/{orderTableId}/clear", id)
                .then().log().all().extract();
    }

    public static void 주문_테이블_생성_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 주문_테이블_착석_상태_변경_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 주문_테이블_인원수_변경_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 주문_테이블_비어있음_상태_변경_완료(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private static Map<String, Object> createParams(String name) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("numberOfGuests", "0");
        params.put("empty", true);
        return params;
    }
}
