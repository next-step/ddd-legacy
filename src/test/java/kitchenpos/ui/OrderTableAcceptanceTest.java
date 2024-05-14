package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.AcceptanceTest;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static kitchenpos.acceptacne.steps.OrderTableSteps.getOrderTablesStep;
import static kitchenpos.acceptacne.steps.OrderTableSteps.주문테이블에_앉는다;
import static kitchenpos.acceptacne.steps.OrderTableSteps.주문테이블을_등록한다;
import static kitchenpos.acceptacne.steps.OrderTableSteps.주문테이블을_초기화한다;
import static kitchenpos.acceptacne.steps.OrderTableSteps.주문테이블의_인원을_변경한다;
import static kitchenpos.fixture.OrderTableFixture.NAME_2번;
import static kitchenpos.fixture.OrderTableFixture.changeNumberOfGuestsRequest;
import static kitchenpos.fixture.OrderTableFixture.orderTableCreateRequest;
import static kitchenpos.fixture.OrderTableFixture.이름_1번;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("주문테이블 인수테스트")
@AcceptanceTest
class OrderTableAcceptanceTest {
    @DisplayName("[성공] 주문테이블을 등록한다.")
    @Test
    void createOrderTable() {
        // given
        OrderTable 주문테이블_1번_등록_요청 = 주문테이블_등록_요청(이름_1번);

        // when
        ExtractableResponse<Response> 주문테이블_1번_등록_응답 = 주문테이블을_등록한다(주문테이블_1번_등록_요청);

        // then
        주문테이블_등록_검증(주문테이블_1번_등록_응답);
    }

    @DisplayName("[성공] 주문테이블에 앉는다.")
    @Test
    void sitOrderTable() {
        // given
        UUID 주문테이블Id = 주문테이블을_등록하고_Id를_반환한다(이름_1번);

        // when
        ExtractableResponse<Response> 주문테이블_응답 = 주문테이블에_앉는다(주문테이블Id);

        // then
        주문테이블에_앉는_검증(주문테이블Id, 주문테이블_응답);
    }

    @DisplayName("[성공] 주문테이블을 초기화한다.")
    @Test
    void clearOrderTable() {
        // given
        UUID 주문테이블Id = 주문테이블을_등록하고_Id를_반환한다(이름_1번);
        OrderTable 인원변경_요청 = 주문테이블의_인원변경을_요청(3);
        주문테이블에_앉는다(주문테이블Id);
        주문테이블의_인원을_변경한다(주문테이블Id, 인원변경_요청);

        // when
        ExtractableResponse<Response> 주문테이블_초기화_응답 = 주문테이블을_초기화한다(주문테이블Id);

        // then
        주문테이블_초기화_검증(주문테이블Id, 주문테이블_초기화_응답);
    }

    @DisplayName("[성공] 주문테이블의 인원을 변경한다.")
    @Test
    void changeNumberOfGuestsOrderTable() {
        // given
        UUID 주문테이블Id = 주문테이블을_등록하고_Id를_반환한다(이름_1번);
        주문테이블에_앉는다(주문테이블Id);
        OrderTable 인원변경_요청 = 주문테이블의_인원변경을_요청(4);

        // when
        ExtractableResponse<Response> 주문테이블_인원변경_응답 = 주문테이블의_인원을_변경한다(주문테이블Id, 인원변경_요청);

        // then
        주문테이블_인원변경_검증(주문테이블Id, 주문테이블_인원변경_응답);
    }

    @DisplayName("[성공] 주문테이블의 목록을 볼 수 있다.")
    @Test
    void getOrderTables() {
        // given
        UUID ORDER_TABLE_1번_ID = 주문테이블을_등록하고_Id를_반환한다(이름_1번);
        UUID ORDER_TABLE_2번_ID = 주문테이블을_등록하고_Id를_반환한다(NAME_2번);

        // when
        ExtractableResponse<Response> response = getOrderTablesStep();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("id", UUID.class))
                        .containsExactly(ORDER_TABLE_1번_ID, ORDER_TABLE_2번_ID),
                () -> assertThat(response.jsonPath().getList("name"))
                        .containsExactly(이름_1번, NAME_2번)
        );
    }

    private static void 주문테이블_등록_검증(ExtractableResponse<Response> 주문테이블_1번_등록_응답) {
        assertAll(
                () -> assertThat(주문테이블_1번_등록_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(주문테이블_1번_등록_응답.jsonPath().getObject("id", UUID.class)).isNotNull(),
                () -> assertThat(주문테이블_1번_등록_응답.jsonPath().getString("name")).isEqualTo(이름_1번),
                () -> assertThat(주문테이블_1번_등록_응답.jsonPath().getInt("numberOfGuests")).isZero(),
                () -> assertThat(주문테이블_1번_등록_응답.jsonPath().getBoolean("occupied")).isFalse()
        );
    }

    private static void 주문테이블에_앉는_검증(UUID 주문테이블Id, ExtractableResponse<Response> 주문테이블_응답) {
        assertAll(
                () -> assertThat(주문테이블_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(주문테이블_응답.jsonPath().getObject("id", UUID.class)).isEqualTo(주문테이블Id),
                () -> assertThat(주문테이블_응답.jsonPath().getString("name")).isEqualTo(이름_1번),
                () -> assertThat(주문테이블_응답.jsonPath().getInt("numberOfGuests")).isNotNegative(),
                () -> assertThat(주문테이블_응답.jsonPath().getBoolean("occupied")).isTrue()
        );
    }

    private static void 주문테이블_초기화_검증(UUID 주문테이블Id, ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isEqualTo(주문테이블Id),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_1번),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isZero(),
                () -> assertThat(response.jsonPath().getBoolean("occupied")).isFalse()
        );
    }

    private static void 주문테이블_인원변경_검증(UUID 주문테이블Id, ExtractableResponse<Response> response) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isEqualTo(주문테이블Id),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_1번),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isEqualTo(4),
                () -> assertThat(response.jsonPath().getBoolean("occupied")).isTrue()
        );
    }

    private static OrderTable 주문테이블_등록_요청(String name) {
        return orderTableCreateRequest(name);
    }

    private static UUID 주문테이블을_등록하고_Id를_반환한다(String name) {
        OrderTable request = 주문테이블_등록_요청(name);
        return 주문테이블을_등록한다(request).as(OrderTable.class).getId();
    }

    private static OrderTable 주문테이블의_인원변경을_요청(int numberOfGuests) {
        return changeNumberOfGuestsRequest(numberOfGuests);
    }
}
