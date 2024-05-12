package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.AcceptanceTest;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static kitchenpos.acceptacne.steps.OrderTableSteps.changeNumberOfGuestsOrderTableStep;
import static kitchenpos.acceptacne.steps.OrderTableSteps.clearOrderTableStep;
import static kitchenpos.acceptacne.steps.OrderTableSteps.createOrderTableStep;
import static kitchenpos.acceptacne.steps.OrderTableSteps.getOrderTablesStep;
import static kitchenpos.acceptacne.steps.OrderTableSteps.sitOrderTableStep;
import static kitchenpos.fixture.OrderTableFixture.이름_1번;
import static kitchenpos.fixture.OrderTableFixture.NAME_2번;
import static kitchenpos.fixture.OrderTableFixture.changeNumberOfGuestsRequest;
import static kitchenpos.fixture.OrderTableFixture.orderTableCreateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("주문테이블 인수테스트")
@AcceptanceTest
class OrderTableAcceptanceTest {
    @DisplayName("주문테이블을 등록한다.")
    @Test
    void createOrderTable() {
        // given
        OrderTable request = orderTableCreateRequest(이름_1번);

        // when
        ExtractableResponse<Response> response = createOrderTableStep(request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_1번),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isZero(),
                () -> assertThat(response.jsonPath().getBoolean("occupied")).isFalse()
        );
    }

    @DisplayName("주문테이블에 앉는다.")
    @Test
    void sitOrderTable() {
        // given
        UUID orderTableId = createOrderId(이름_1번);

        // when
        ExtractableResponse<Response> response = sitOrderTableStep(orderTableId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isEqualTo(orderTableId),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_1번),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isNotNegative(),
                () -> assertThat(response.jsonPath().getBoolean("occupied")).isTrue()
        );
    }

    @DisplayName("주문테이블을 초기화한다.")
    @Test
    void clearOrderTable() {
        // given
        UUID orderTableId = createOrderId(이름_1번);
        OrderTable changeRequest = changeNumberOfGuestsRequest(3);
        sitOrderTableStep(orderTableId);
        changeNumberOfGuestsOrderTableStep(orderTableId, changeRequest);

        // when
        ExtractableResponse<Response> response = clearOrderTableStep(orderTableId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isEqualTo(orderTableId),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_1번),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isZero(),
                () -> assertThat(response.jsonPath().getBoolean("occupied")).isFalse()
        );
    }

    @DisplayName("주문테이블의 인원을 변경한다.")
    @Test
    void changeNumberOfGuestsOrderTable() {
        // given
        UUID orderTableId = createOrderId(이름_1번);
        sitOrderTableStep(orderTableId);
        OrderTable request = changeNumberOfGuestsRequest(4);

        // when
        ExtractableResponse<Response> response = changeNumberOfGuestsOrderTableStep(orderTableId, request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isEqualTo(orderTableId),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_1번),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isEqualTo(4),
                () -> assertThat(response.jsonPath().getBoolean("occupied")).isTrue()
        );
    }

    @DisplayName("주문테이블의 목록을 볼 수 있다.")
    @Test
    void getOrderTables() {
        // given
        UUID ORDER_TABLE_1번_ID = createOrderId(이름_1번);
        UUID ORDER_TABLE_2번_ID = createOrderId(NAME_2번);

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

    private static UUID createOrderId(String name1번) {
        OrderTable request = orderTableCreateRequest(name1번);
        return createOrderTableStep(request).as(OrderTable.class).getId();
    }
}
