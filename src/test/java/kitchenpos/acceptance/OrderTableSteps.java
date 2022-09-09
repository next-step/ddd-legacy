package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import kitchenpos.domain.OrderTable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

public class OrderTableSteps {

    public static ExtractableResponse<Response> 테이블_등록_요청(final RequestSpecification given, final OrderTable orderTable) {
        return given.body(orderTable)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/api/order-tables")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    public static UUID 테이블이_등록됨(final RequestSpecification given, final String name) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setName(name);

        return 테이블_등록_요청(given, orderTable).jsonPath().getUUID("id");
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


    public static ExtractableResponse<Response> 테이블에_앉은_손님인원을_수정_요청(final RequestSpecification given, final UUID id, final OrderTable orderTable) {
        return given.body(orderTable)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/order-tables/{id}/number-of-guests", id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    public static ExtractableResponse<Response> 테이블에_앉은_손님인원이_수정됨(final RequestSpecification given, final UUID id, final int numberOfGuests) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);

        return given.body(orderTable)
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
