package kitchenpos.application;

import kitchenpos.application.fake.FakeOrderRepository;
import kitchenpos.application.fake.FakeOrderTableRepository;
import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderTableServiceTest {

    private final OrderTableRepository orderTableRepository = new FakeOrderTableRepository();
    private final OrderRepository orderRepository = new FakeOrderRepository();
    private final OrderTableService orderTableService = new OrderTableService(orderTableRepository, orderRepository);

    @Test
    @DisplayName("주문 테이블을 생성하여 저장한다.")
    void createOrderTable() {
        // given
        OrderTable orderTableRequest = new OrderTable();
        orderTableRequest.setName("주문 테이블");

        // when
        OrderTable orderTable = orderTableService.create(orderTableRequest);

        // then
        assertAll(
                () -> assertThat(orderTable.getId()).isNotNull(),
                () -> assertThat(orderTable.getName()).isEqualTo("주문 테이블"),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(orderTable.isOccupied()).isFalse()
        );
    }

    @Test
    @DisplayName("주문 테이블을 착석 상태로 변경한다.")
    void sitOrderTable() {
        // given
        OrderTable orderTable = createOrderTable("주문 테이블");

        // when
        orderTableService.sit(orderTable.getId());

        // then
        assertThat(orderTable.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("주문 테이블을 빈 테이블로 변경한다.")
    void clearOrderTable() {
        // given
        OrderTable orderTable = createOrderTable("주문 테이블");

        createOrder(orderTable, OrderStatus.COMPLETED);

        // when
        orderTableService.clear(orderTable.getId());

        // then
        assertAll(
                () -> assertThat(orderTable.getId()).isNotNull(),
                () -> assertThat(orderTable.getName()).isEqualTo("주문 테이블"),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(orderTable.isOccupied()).isFalse()
        );
    }

    @Test
    @DisplayName("주문 테이블을 빈 테이블로 변경한다. - 사용 중 일경우 예외 발생")
    void clearOrderTableIfNotCompletedThrowException() {
        // given
        OrderTable orderTable = createOrderTable("주문 테이블");
        createOrder(orderTable, OrderStatus.SERVED);

        // when
        // then
        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    @DisplayName("주문 테이블의 이용자 수를 변경한다.")
    void updateOrderTableNumberOfGuests() {
        // given
        OrderTable orderTable = createOrderTable("주문 테이블");
        OrderTable changeNumberOfGuests = new OrderTable();
        changeNumberOfGuests.setNumberOfGuests(5);
        UUID id = orderTable.getId();
        orderTableService.sit(id);

        // when
        orderTableService.changeNumberOfGuests(id, changeNumberOfGuests);

        // then
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(5);
    }

    @Test
    @DisplayName("주문 테이블 리스트를 가져온다.")
    void findOrderTables() {
        // given
        createOrderTable("주문 테이블");
        createOrderTable("주문 테이블2");
        createOrderTable("주문 테이블3");

        // when
        List<OrderTable> orderTables = orderTableService.findAll();

        // then
        assertAll(
                () -> assertThat(orderTables).isNotEmpty(),
                () -> assertThat(orderTables.size()).isEqualTo(3)
        );
    }

    private OrderTable createOrderTable(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTableService.create(orderTable);
    }
    
    private Order createOrder(OrderTable orderTable, OrderStatus orderStatus) {
        Order order = new Order();
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(orderStatus);
        order.setType(OrderType.EAT_IN);
        return orderRepository.save(order);
    }
}
