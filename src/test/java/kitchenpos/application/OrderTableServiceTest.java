package kitchenpos.application;

import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTableServiceTest extends InitTest {
    @Resource
    private OrderTableService target;

    @Test
    @DisplayName("테이블은 이름과 손님 수를 가진다.")
    void create() {
        OrderTable request = buildValidOrderTable();

        target.create(request);
    }

    @Test
    @DisplayName("해당 홀 주문이 COMPLETE 되지 않은 테이블은 치울 수 없다.")
    void cannotClearYetCompleteOrderTable() {
        assertThatThrownBy(() -> {
            target.clear(ORDER_TABLE_ID);
        })
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("테이블의 손님 수는 변경 가능하다.")
    void changeNumberOfGuests() {
        OrderTable request = buildOccupiedOrderTable();
        request.setNumberOfGuests(2);
        OrderTable result = target.changeNumberOfGuests(OCCUPIED_ORDER_TABLE_ID, request);

        assertThat(result.getNumberOfGuests()).isEqualTo(2);
    }

    @Test
    @DisplayName("점유된 테이블만 손님 수를 양수로 변경 가능하다.")
    void cannotChangeNumberOfGuestsOfUnoccupiedTable() {
        OrderTable request = buildValidOrderTable();
        assertThatThrownBy(() -> {
            target.changeNumberOfGuests(ORDER_TABLE_ID, request);
        })
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("해당 홀 주문이 COMPLETE 되지 않은 테이블은 치울 수 없다.")
    void cannotClearEatInOrderNotCompleteTable() {
        assertThatThrownBy(() -> {
            target.clear(ORDER_TABLE_ID);
        })
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("테이블을 치우면 앉은 손님수가 0명이 되고, 점유가 해제된다.")
    void clearGuestsNumberAndOccupied() {
        OrderTable result = target.clear(OCCUPIED_ORDER_TABLE_ID);

        assertThat(result.getNumberOfGuests()).isZero();
        assertThat(result.isOccupied()).isFalse();
    }
}