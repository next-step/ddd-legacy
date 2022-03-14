package kitchenpos.application;

import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;

    @DisplayName("테이블을 지정하는 이름을 입력해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void nameIsMandatory(String name) {
        // given
        OrderTable orderTableCreateRequest = createOrderTableCreateRequest(name);

        // when then
        assertThatThrownBy(() -> orderTableService.create(orderTableCreateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("착석하는 손님의 수는 0보다 작을 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {-1})
    void negativeNumberOfGuests(int number) {
        // given
        OrderTable orderTableCreateRequest = createOrderTableCreateRequest("1번");
        OrderTable orderTable = orderTableService.create(orderTableCreateRequest);

        // when
        OrderTable guestNumberChangeRequest = new OrderTable();
        guestNumberChangeRequest.setNumberOfGuests(number);

        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), guestNumberChangeRequest))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("비어있는 테이블의 손님 수를 변경하는 것은 불가능하다.")
    @Test
    void changeNumberOfEmptyTable() {
        // given
        OrderTable orderTableCreateRequest = createOrderTableCreateRequest("1번");
        OrderTable orderTable = orderTableService.create(orderTableCreateRequest);

        // when
        OrderTable guestNumberChangeRequest = new OrderTable();
        guestNumberChangeRequest.setNumberOfGuests(2);

        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), guestNumberChangeRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블 착석 후 손님 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        // given
        OrderTable orderTableCreateRequest = createOrderTableCreateRequest("1번");
        OrderTable orderTable = orderTableService.create(orderTableCreateRequest);

        // when
        orderTable = orderTableService.sit(orderTable.getId());

        OrderTable guestNumberChangeRequest = new OrderTable();
        guestNumberChangeRequest.setNumberOfGuests(2);
        orderTable = orderTableService.changeNumberOfGuests(orderTable.getId(), guestNumberChangeRequest);

        // then
        assertThat(orderTable.isEmpty()).isFalse();
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(2);
    }

    public static OrderTable createOrderTableCreateRequest(String name) {
        OrderTable orderTableCreateRequest = new OrderTable();
        orderTableCreateRequest.setName(name);
        return orderTableCreateRequest;
    }
}
