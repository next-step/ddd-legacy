package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.exception.OrderTableExceptionMessage.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class OrderTableServiceTest {


    private final OrderTableRepository orderTableRepository = new FakeOrderTableRepository();
    private final OrderRepository orderRepository = new FakeOrderRepository();
    private final OrderTableService service = new OrderTableService(orderTableRepository, orderRepository);

    @DisplayName("주문테이블 이름이 null 이거나 비어있으면 예외를 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create_name_empty(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);

        assertThatThrownBy(() -> service.create(orderTable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ORDER_TABLE_NAME_EMPTY);
    }

    @DisplayName("주문테이블 생성 성공")
    @Test
    void create_success() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName("1번테이블");

        OrderTable result = service.create(orderTable);

        assertThat(result.getNumberOfGuests()).isEqualTo(0);
        assertThat(result.isOccupied()).isFalse();
    }

    @DisplayName("주문테이블에 착석시 테이블이 없으면 예외를 발생시킨다.")
    @Test
    void sit_not_found_orderTable() {
        assertThatThrownBy(() -> service.sit(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(NOT_FOUND_ORDER_TABLE);
    }

    @DisplayName("주문테이블에 착석시 테이블이 없으면 예외를 발생시킨다.")
    @Test
    void sit_success() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName("1번테이블");
        orderTableRepository.save(orderTable);

        OrderTable result = service.sit(orderTable.getId());

        assertThat(result.isOccupied()).isTrue();
    }

    @DisplayName("주문테이블을 비울때 주문완료된 테이블이 있으면 예외를 발생시킨다.")
    @Test
    void clear_not_found_completed_order_table() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번테이블");
        orderTableRepository.save(orderTable);

        Order order = new OrderBuilder()
                .orderTable(orderTable)
                .orderTableId(orderTable.getId())
                .status(OrderStatus.WAITING)
                .build();
        orderRepository.save(order);

        assertThatThrownBy(() -> service.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(NOT_EXIST_COMPLETE_ORDER_TABLE);
    }

    @DisplayName("주문테이블을 비우기 성공")
    @Test
    void clear_success() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번테이블");
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(10);
        orderTableRepository.save(orderTable);

        Order order = new OrderBuilder()
                .orderTable(orderTable)
                .orderTableId(orderTable.getId())
                .status(OrderStatus.COMPLETED)
                .build();
        orderRepository.save(order);

        OrderTable result = service.clear(orderTable.getId());

        assertThat(result.getNumberOfGuests()).isEqualTo(0);
        assertThat(result.isOccupied()).isFalse();
    }

    @DisplayName("테이블인원수 변경시 인원수가 음수면 예외를 발생시킨다.")
    @Test
    void change_numberOfGuests_negative() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번테이블");
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(10);
        orderTableRepository.save(orderTable);

        OrderTable request = new OrderTable();
        request.setNumberOfGuests(-1);

        assertThatThrownBy(() -> service.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(NUMBER_GUESTS_NEGATIVE);
    }

    @DisplayName("테이블인원수 변경시 인원이 가득차있지 않으면 예외를 발생시킨다.")
    @Test
    void change_numberOfGuests_not_occupied() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번테이블");
        orderTable.setOccupied(false);
        orderTable.setNumberOfGuests(10);
        orderTableRepository.save(orderTable);

        OrderTable request = new OrderTable();
        request.setNumberOfGuests(15);

        assertThatThrownBy(() -> service.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(NOT_OCCUPIED);
    }

    @DisplayName("테이블인원수 변경 성공")
    @Test
    void change_numberOfGuests_success() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번테이블");
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(10);
        orderTableRepository.save(orderTable);

        OrderTable request = new OrderTable();
        request.setNumberOfGuests(15);

        OrderTable result = service.changeNumberOfGuests(orderTable.getId(), request);

        assertThat(result.getNumberOfGuests()).isEqualTo(15);
    }


}
