package kitchenpos.ui;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("주문 테이블 API")
class OrderTableRestControllerTest extends ControllerTest {

    private OrderTable orderTable;

    @DisplayName("주문 테이블 API 테스트")
    @TestFactory
    Stream<DynamicNode> table() {
        return Stream.of(
                dynamicTest("빈 테이블을 등록한다.", () -> {
                    ExtractableResponse<Response> response = 테이블_생성_요청("주문테이블1");

                    테이블_생성됨(response);
                    orderTable = response.as(OrderTable.class);
                }),
                dynamicTest("사용 상태가 아닌 주문 테이블의 손님 수를 변경한다.", () -> {
                    ExtractableResponse<Response> response = 테이블_손님_수_변경_요청(orderTable, 5);

                    테이블_손님_수_변경_실패됨(response);
                }),
                dynamicTest("빈 테이블을 주문 테이블로 변경한다.", () -> {
                    ExtractableResponse<Response> response = 테이블_사용상태_변경_요청(orderTable);

                    테이블_상태_변경됨(response);
                }),
                dynamicTest("주문 테이블의 손님 수를 변경한다.", () -> {
                    ExtractableResponse<Response> response = 테이블_손님_수_변경_요청(orderTable, 5);

                    테이블_손님_수_변경됨(response);
                }),
                dynamicTest("주문 테이블의 손님 수를 0미만으로 변경한다.", () -> {
                    ExtractableResponse<Response> response = 테이블_손님_수_변경_요청(orderTable, -1);

                    테이블_손님_수_변경_실패됨(response);
                }),
                dynamicTest("존재하지 않는 테이블의 손님 수를 변경한다.", () -> {
                    OrderTable orderTableByNotExist = new OrderTable();
                    orderTableByNotExist.setId(UUID.randomUUID());

                    ExtractableResponse<Response> response = 테이블_손님_수_변경_요청(orderTableByNotExist, 5);

                    테이블_손님_수_변경_실패됨(response);
                }),
                dynamicTest("테이블을 미사용상태로 변경힌디.", () -> {
                    ExtractableResponse<Response> response = 테이블_미사용상태_변경_요청(orderTable);

                    테이블_상태_변경됨(response);
                }),
                dynamicTest("테이블 목록을 조회한다.", () -> {
                    ExtractableResponse<Response> response = 테이블_목록_조회_요청();

                    테이블_목록_응답됨(response);
                    테이블_목록_응답됨(response, orderTable);
                })
        );
    }

    public static ExtractableResponse<Response> 테이블_생성_요청(String name) {
        Map<String, Object> request = new HashMap<>();
        request.put("name", name);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/api/order-tables")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 테이블_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/api/order-tables")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 테이블_손님_수_변경_요청(OrderTable orderTable, int numberOfGuests) {
        Map<String, Object> request = new HashMap<>();
        request.put("numberOfGuests", numberOfGuests);

        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/order-tables/{orderTableId}/number-of-guests", orderTable.getId())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 테이블_사용상태_변경_요청(OrderTable orderTable) {

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/order-tables/{orderTableId}/sit", orderTable.getId())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 테이블_미사용상태_변경_요청(OrderTable orderTable) {

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/order-tables/{orderTableId}/clear", orderTable.getId())
                .then().log().all()
                .extract();
    }

    public static void 테이블_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    public static void 테이블_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 테이블_목록_응답됨(ExtractableResponse<Response> response, OrderTable... orderTables) {
        List<UUID> orderTableIds = response.jsonPath().getList(".", OrderTable.class)
                .stream()
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        List<UUID> expectedIds = Arrays.stream(orderTables)
                .map(OrderTable::getId)
                .collect(Collectors.toList());

        assertThat(orderTableIds).containsExactlyElementsOf(expectedIds);
    }

    public static void 테이블_손님_수_변경됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 테이블_손님_수_변경_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 테이블_상태_변경됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
