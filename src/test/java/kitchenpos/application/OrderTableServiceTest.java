package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fake.InMemoryOrderRepository;
import kitchenpos.fake.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kitchenpos.dummy.DummyOrderTable.createOrderTable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class OrderTableServiceTest {

    OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    OrderRepository orderRepository = new InMemoryOrderRepository();
    OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문 테이블을 등록한다.")
    @Test
    void create() {
        OrderTable orderTable = createOrderTable();
        OrderTable actual = orderTableService.create(orderTable);
        assertAll(
                () -> assertNotNull(actual),
                () -> assertNotNull(actual.getId()),
                () -> assertEquals(orderTable.getName(), actual.getName()));

    }


    @DisplayName("주문 테이블에 손님이 앉는다.")
    @Test
    void sit() {
        OrderTable orderTable = orderTableService.create(createOrderTable());
        assertThat(orderTable.isOccupied()).isFalse();
        OrderTable actual = orderTableService.sit(orderTable.getId());
        assertAll(
                () -> assertNotNull(actual),
                () -> assertNotNull(actual.getId()),
                () -> assertEquals(orderTable.isOccupied(), true));
    }

    @DisplayName("주문 테이블의 손님이 모두 나간다.")
    @Test
    void clear() {
        OrderTable orderTable = orderTableService.create(createOrderTable());
        OrderTable sit = orderTableService.sit(orderTable.getId());
        assertThat(orderTable.isOccupied()).isTrue();
        OrderTable actual = orderTableService.clear(sit.getId());
        assertAll(
                () -> assertNotNull(actual),
                () -> assertNotNull(actual.getId()),
                () -> assertEquals(orderTable.isOccupied(), false));
    }

    @DisplayName("손님이 앉지 않은 테이블은 손님 수를 변경할 수 없다.")
    @Test
    void changeNumberOfGuests_false() {
        OrderTable orderTable = orderTableService.create(createOrderTable());
        assertThat(orderTable.getNumberOfGuests()).isZero();
        orderTable.setNumberOfGuests(10);
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("손님이 앉은 테이블은 손님 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests_true() {
        OrderTable orderTable = orderTableService.create(createOrderTable());
        orderTableService.sit(orderTable.getId());
        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isOccupied()).isTrue();
        orderTable.setNumberOfGuests(10);
        OrderTable actual = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);
        assertAll(
                () -> assertNotNull(actual),
                () -> assertNotNull(actual.getId()),
                () -> assertEquals(orderTable.getNumberOfGuests(), 10));
    }


    @Test
    void findAll() {
        orderTableService.create(createOrderTable("테이블1"));
        orderTableService.create(createOrderTable("테이블2"));
        orderTableService.create(createOrderTable("테이블3"));
        assertThat(orderTableService.findAll().size()).isSameAs(3);
    }
}