package kitchenpos.acceptacne.ui;

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
import static kitchenpos.fixture.OrderTableFixture.NAME_1번;
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
        OrderTable request = orderTableCreateRequest(NAME_1번);

        // when
        ExtractableResponse<Response> response = createOrderTableStep(request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(NAME_1번),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isZero(),
                () -> assertThat(response.jsonPath().getBoolean("occupied")).isFalse()
        );
    }

    @DisplayName("주문테이블에 앉는다.")
    @Test
    void sitOrderTable() {
        // given
        OrderTable request = orderTableCreateRequest(NAME_1번);
        OrderTable ORDER_TABLE_1번 = createOrderTableStep(request).as(OrderTable.class);

        // when
        ExtractableResponse<Response> response = sitOrderTableStep(ORDER_TABLE_1번.getId());

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isEqualTo(ORDER_TABLE_1번.getId()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(NAME_1번),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isNotNegative(),
                () -> assertThat(response.jsonPath().getBoolean("occupied")).isTrue()
        );
    }

    @DisplayName("주문테이블을 초기화한다.")
    @Test
    void clearOrderTable() {
        // given
        OrderTable request = orderTableCreateRequest(NAME_1번);
        OrderTable ORDER_TABLE_1번 = createOrderTableStep(request).as(OrderTable.class);
        OrderTable changeRequest = changeNumberOfGuestsRequest(3);
        sitOrderTableStep(ORDER_TABLE_1번.getId());
        changeNumberOfGuestsOrderTableStep(ORDER_TABLE_1번.getId(), changeRequest);

        // when
        ExtractableResponse<Response> response = clearOrderTableStep(ORDER_TABLE_1번.getId());

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isEqualTo(ORDER_TABLE_1번.getId()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(NAME_1번),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isZero(),
                () -> assertThat(response.jsonPath().getBoolean("occupied")).isFalse()
        );
    }

    @DisplayName("주문테이블의 인원을 변경한다.")
    @Test
    void changeNumberOfGuestsOrderTable() {
        // given
        OrderTable createRequest = orderTableCreateRequest(NAME_1번);
        OrderTable ORDER_TABLE_1번 = createOrderTableStep(createRequest).as(OrderTable.class);
        sitOrderTableStep(ORDER_TABLE_1번.getId());
        OrderTable request = changeNumberOfGuestsRequest(4);

        // when
        ExtractableResponse<Response> response = changeNumberOfGuestsOrderTableStep(ORDER_TABLE_1번.getId(), request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isEqualTo(ORDER_TABLE_1번.getId()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(NAME_1번),
                () -> assertThat(response.jsonPath().getInt("numberOfGuests")).isEqualTo(4),
                () -> assertThat(response.jsonPath().getBoolean("occupied")).isTrue()
        );
    }

    @DisplayName("주문테이블의 목록을 볼 수 있다.")
    @Test
    void getOrderTables() {
        // given
        OrderTable createRequest1 = orderTableCreateRequest(NAME_1번);
        OrderTable ORDER_TABLE_1번 = createOrderTableStep(createRequest1).as(OrderTable.class);
        OrderTable createRequest2 = orderTableCreateRequest(NAME_2번);
        OrderTable ORDER_TABLE_2번 = createOrderTableStep(createRequest2).as(OrderTable.class);

        // when
        ExtractableResponse<Response> response = getOrderTablesStep();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("id", UUID.class)).hasSize(2)
                        .containsExactly(ORDER_TABLE_1번.getId(), ORDER_TABLE_2번.getId()),
                () -> assertThat(response.jsonPath().getList("name")).hasSize(2)
                        .containsExactly(NAME_1번, NAME_2번)
        );
    }
}
