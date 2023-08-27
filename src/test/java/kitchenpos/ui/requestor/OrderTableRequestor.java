package kitchenpos.ui.requestor;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.OrderTable;
import org.springframework.http.MediaType;

import java.util.UUID;

import static kitchenpos.objectmother.OrderTableMaker.테이블_고객_4명;

public class OrderTableRequestor {

    private static final String DEFAULT_URL = "/api/order-tables";

    public static ExtractableResponse<Response> 테이블생성요청(OrderTable orderTable) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(orderTable)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract();
    }

    public static UUID 테이블생성요청_테이블식별번호반환(OrderTable orderTable) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(orderTable)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract().jsonPath().getObject("id", UUID.class);
    }

    public static OrderTable 테이블생성요청_테이블반환(OrderTable orderTable) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(orderTable)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract().jsonPath().getObject("$", OrderTable.class);
    }

    public static OrderTable 테이블생성_착석_고객수_요청테이블반환(OrderTable orderTable) {
        OrderTable table = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(orderTable)
                .when().post(DEFAULT_URL)
                .then().log().all()
                .extract().jsonPath().getObject("$", OrderTable.class);

        테이블착석요청(table.getId()).jsonPath().getObject("$", OrderTable.class);
        return 테이블고객수요청(table.getId(), orderTable).jsonPath().getObject("$", OrderTable.class);
    }

    public static ExtractableResponse<Response> 테이블착석요청(UUID orderTableId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("orderTableId", orderTableId)
                .when().put(DEFAULT_URL + "/{orderTableId}/sit")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 테이블청소요청(UUID orderTableId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("orderTableId", orderTableId)
                .when().put(DEFAULT_URL + "/{orderTableId}/clear")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 테이블고객수요청(UUID orderTableId, OrderTable orderTable) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("orderTableId", orderTableId)
                .body(orderTable)
                .when().put(DEFAULT_URL + "/{orderTableId}/number-of-guests")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 테이블전체조회요청() {
        return RestAssured.given().log().all()
                .when().get(DEFAULT_URL)
                .then().log().all()
                .extract();
    }

}
