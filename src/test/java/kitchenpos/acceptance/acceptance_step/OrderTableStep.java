package kitchenpos.acceptance.acceptance_step;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.OrderTable;
import kitchenpos.test_fixture.OrderTableTestFixture;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderTableStep {
    private OrderTableStep() {
    }

    public static ExtractableResponse<Response> 주문_테이블을_등록한다(OrderTable orderTable) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(orderTable)
                .when().post("/api/order-tables")
                .then().log().all()
                .extract();
    }

    public static void 주문_테이블_등록됐다(ExtractableResponse<Response> response, String expectedName, int expectedNumberOfGuests, boolean expectedOccupied) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/api/order-tables/" + response.body().as(OrderTable.class).getId());
        OrderTable 등록된_주문_테이블 = response.body().as(OrderTable.class);
        assertThat(등록된_주문_테이블.getId()).isNotNull();
        assertThat(등록된_주문_테이블.getName()).isEqualTo(expectedName);
        assertThat(등록된_주문_테이블.getNumberOfGuests()).isEqualTo(expectedNumberOfGuests);
        assertThat(등록된_주문_테이블.isOccupied()).isEqualTo(expectedOccupied);
    }

    public static void 주문_테이블_등록에_실패한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static OrderTable 주문_테이블_등록된_상태다() {
        OrderTable orderTable = OrderTableTestFixture.create()
                .changeId(null)
                .changeNumberOfGuests(0)
                .changeName("테이블1")
                .changeOccupied(false)
                .getOrderTable();
        return 주문_테이블을_등록한다(orderTable).body().as(OrderTable.class);
    }

    public static ExtractableResponse<Response> 주문_테이블을_손님이_앉은_상태로_변경한다(OrderTable orderTable) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/order-tables/" + orderTable.getId() + "/sit")
                .then().log().all()
                .extract();
    }

    public static void 주문_테이블_손님이_앉은_상태로_변경에_성공한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        OrderTable orderTable = response.body().as(OrderTable.class);
        assertThat(orderTable.isOccupied()).isTrue();
    }

    public static OrderTable 주문_테이블이_등록된_상태가_아니다() {
        return OrderTableTestFixture.create()
                .changeId(UUID.randomUUID())
                .changeNumberOfGuests(0)
                .changeName("테이블1")
                .changeOccupied(false)
                .getOrderTable();
    }

    public static void 주문_테이블을_손님이_앉은_상태로_변경에_실패한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 주문_테이블을_비어있는_상태로_변경한다(OrderTable orderTable) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/api/order-tables/" + orderTable.getId() + "/clear")
                .then().log().all()
                .extract();
    }

    public static void 주문_테이블을_비어있는_상태로_변경에_성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        OrderTable orderTable = response.body().as(OrderTable.class);
        assertThat(orderTable.isOccupied()).isFalse();
        assertThat(orderTable.getNumberOfGuests()).isZero();
    }

    public static void 주문_테이블을_비어있는_상태로_변경에_실패한다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 주문_테이블에_주문이_완료되지않아_비어있는_상태로_변경에_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 주문_테이블에_앉은_손님_수를_변경한다(OrderTable orderTable) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(orderTable)
                .when().put("/api/order-tables/" + orderTable.getId() + "/number-of-guests")
                .then().log().all()
                .extract();
    }

    public static void 주문_테이블에_앉은_고객_수_변경에_성공했다(ExtractableResponse<Response> response, int expectedNumberOfGuests) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        OrderTable orderTable = response.body().as(OrderTable.class);
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(expectedNumberOfGuests);
    }

    public static void 주문_테이블에_앉은_고객_수_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static void 주문_테이블이_등록되지_않아_앉은_고객_수_변경에_실패했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static ExtractableResponse<Response> 등록된_전체_주문_테이블_정보를_조회한다() {
        return given().log().all()
                .when().get("/api/order-tables")
                .then().log().all()
                .extract();
    }

    public static void 등록된_전체_주문_테이블_정보를_조회에_성공했다(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        response.body().jsonPath().getList(".", OrderTable.class).forEach(orderTable -> {
            assertThat(orderTable.getId()).isNotNull();
            assertThat(orderTable.getName()).isNotNull();
            assertThat(orderTable.getNumberOfGuests()).isNotNull();
            assertThat(orderTable.isOccupied()).isNotNull();
        });
    }
}
