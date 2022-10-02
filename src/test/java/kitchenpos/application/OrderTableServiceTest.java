package kitchenpos.application;

import kitchenpos.application.fake.FakeOrderRepository;
import kitchenpos.application.fake.FakeOrderTableRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTableServiceTest {

    private final OrderTableRepository orderTableRepository = new FakeOrderTableRepository();
    private final OrderRepository orderRepository = new FakeOrderRepository();

    private final OrderTableService service = new OrderTableService(orderTableRepository, orderRepository);

    @Test
    @DisplayName("`주문 테이블`은 이름을 입력하여 등록할 수 있다.")
    void create() {

        String name = "테이블1";
        OrderTable orderTable = createOrderTable(name);

        OrderTable savedOrderTable = service.create(orderTable);

        assertThat(savedOrderTable.getId()).isNotNull();
        assertThat(savedOrderTable.getName()).isEqualTo(name);
        assertThat(savedOrderTable.isOccupied()).isFalse();
        assertThat(savedOrderTable.getNumberOfGuests()).isZero();
    }

    @ParameterizedTest
    @DisplayName("`주문 테이블`의 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    void create_not_null_and_empty(String name) {

        OrderTable orderTable = createOrderTable(name);

        assertThatThrownBy(() -> service.create(orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("고객을 `주문 테이블` 자리에 앉게 할 수 있다.")
    void sit() {

        String name = "테이블1";
        OrderTable orderTable = createOrderTable(name);

        OrderTable savedOrderTable = service.create(orderTable);

        OrderTable sitOrderTable = service.sit(savedOrderTable.getId());

        assertThat(sitOrderTable.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("`주문 테이블`이 없으면 자리에 앉게 할 수 없다.")
    void sit_order_table_not_found() {

        assertThatThrownBy(() -> service.sit(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("`주문 테이블`의 자리를 비울 수 있다.")
    void clear() {

        String name = "테이블1";
        OrderTable orderTable = createOrderTable(name);

        OrderTable savedOrderTable = service.create(orderTable);

        OrderTable sitOrderTable = service.sit(savedOrderTable.getId());
        OrderTable clearOrderTable = service.clear(sitOrderTable.getId());

        assertThat(clearOrderTable.isOccupied()).isFalse();
        assertThat(clearOrderTable.getNumberOfGuests()).isZero();
    }
    @Test
    @DisplayName("`주문 테이블`의 완료되지 않은 `주문`이 있다면 비울 수 없다.")
    void clear_order_status_not_complete() {

        String name = "테이블1";
        OrderTable orderTable = createOrderTable(name);
        OrderTable savedOrderTable = service.create(orderTable);
        orderRepository.save(createOrder(OrderStatus.WAITING, savedOrderTable));
        OrderTable sitOrderTable = service.sit(savedOrderTable.getId());

        assertThatThrownBy(() -> service.clear(sitOrderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("`주문 테이블`이 없다면 비울 수 없다.")
    void clear_order_table_not_found() {

        assertThatThrownBy(() -> service.clear(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("`주문 테이블`에 앉은 고객 수를 수정할 수 있다.")
    void changeNumberOfGuests() {

        String name = "테이블1";
        OrderTable orderTable = createOrderTable(name);
        OrderTable savedOrderTable = service.create(orderTable);
        OrderTable sitOrderTable = service.sit(savedOrderTable.getId());

        int numberOfGuests = 5;
        sitOrderTable.setNumberOfGuests(numberOfGuests);

        OrderTable changeNumberOfGuestsOrderTable = service.changeNumberOfGuests(sitOrderTable.getId(), sitOrderTable);

        assertThat(changeNumberOfGuestsOrderTable.getNumberOfGuests()).isEqualTo(numberOfGuests);
    }


    @ParameterizedTest
    @DisplayName("고객의 수는 0이하의 수를 입력할 수 없다.")
    @MethodSource("negativeNumberOfGuestMethodSource")
    void changeNumberOfGuests_not_zero(int numberOfGuests) {

        String name = "테이블1";
        OrderTable orderTable = createOrderTable(name);
        OrderTable savedOrderTable = service.create(orderTable);
        OrderTable sitOrderTable = service.sit(savedOrderTable.getId());

        sitOrderTable.setNumberOfGuests(numberOfGuests);

        assertThatThrownBy(() -> service.changeNumberOfGuests(sitOrderTable.getId(), sitOrderTable))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("사용중이지 않은 `주문 테이블`은 고객 수를 변경할 수 없다.")
    void changeNumberOfGuests_not_is() {

        String name = "테이블1";
        OrderTable orderTable = createOrderTable(name);
        OrderTable savedOrderTable = service.create(orderTable);

        int numberOfGuests = 5;
        savedOrderTable.setNumberOfGuests(numberOfGuests);

        assertThatThrownBy(() -> service.changeNumberOfGuests(savedOrderTable.getId(), savedOrderTable))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("등록한 모든 `주문 테이블`을 조회할 수 있다.")
    void findAll() {
        OrderTable savedOrderTable1 = service.create(createOrderTable("테이블1"));
        OrderTable savedOrderTable2 = service.create(createOrderTable("테이블1"));

        List<OrderTable> savedOrderTables = Lists.list(savedOrderTable1, savedOrderTable2);

        List<OrderTable> orderTables = service.findAll();
        assertThat(orderTables).containsAll(savedOrderTables);
    }


    private Order createOrder(OrderStatus orderStatus, OrderTable orderTable) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(orderStatus);
        order.setOrderTable(orderTable);

        return order;
    }


    private OrderTable createOrderTable(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);

        return orderTable;
    }

    static Stream<Integer> negativeNumberOfGuestMethodSource() {
        return Stream.of(-1);
    }
}