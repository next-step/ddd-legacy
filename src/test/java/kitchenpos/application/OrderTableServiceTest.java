package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.exception.GuestLessThanZeroException;
import kitchenpos.exception.OrderTabmeIsEmptyException;
import kitchenpos.repository.InMemoryOrderRepository;
import kitchenpos.repository.InMemoryOrderTableRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("[주문 테이블]")
@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        this.orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Test
    @DisplayName("주문 테이블을 생성한다")
    void createOrderTableTest() {
        final OrderTable orderTableRequest = createOrderTable("1번 테이블");

        OrderTable actual = orderTableService.create(orderTableRequest);

        assertAll(
                () -> Assertions.assertThat(actual.getId()).isNotNull(),
                () -> Assertions.assertThat(actual.getName()).isEqualTo(orderTableRequest.getName())
        );
    }


    @Test
    @DisplayName("주문 테이블의 이름은 필수로 있어야 한다.")
    void createOrderTableNameTest() {
        final OrderTable orderTableRequest = createOrderTable();

        AssertionsForClassTypes.assertThatThrownBy(() -> orderTableService.create(orderTableRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /*
     * 주문 테이블의 착석 상태를 변경한다.
     * */

    @Test
    @DisplayName("주문 테이블을 착석 상태(false)로 변경")
    void orderTableSitTest() {
        final OrderTable orderTable = createOrderTable(true);

        orderTableRepository.save(orderTable);

        final OrderTable actual = orderTableService.sit(orderTable.getId());
        Assertions.assertThat(actual.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("주문 테이블을 착성 상태로 변경시 주문 테이블의 정보가 있어야 한다")
    void orderTableSitNotFoundTest() {

        final OrderTable orderTable = createOrderTable();

        AssertionsForClassTypes.assertThatThrownBy(() -> orderTableService.sit(orderTable.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("주문 테이블을 공석 상태(true)로 변경")
    void orderTableClearTest() {
        final OrderTable orderTable = createOrderTable(false, 4);
        final Order order = createOrder(OrderStatus.COMPLETED, orderTable);

        orderTableRepository.save(orderTable);
        orderRepository.save(order);

        OrderTable actual = orderTableService.clear(orderTable.getId());

        assertAll(
                () -> Assertions.assertThat(actual.isEmpty()).isTrue(),
                () -> Assertions.assertThat(actual.getNumberOfGuests()).isZero()
        );
    }

    /*
     * 주문 테이블의 인원 변경
     * */

    @Test
    @DisplayName("주문 테이블의 변경 인원이 0보다 작으면 안된다.")
    void orderTableChangeNumberOfGuestsTest() {
        final OrderTable orderTableRequest = createOrderTable(-1);
        final OrderTable orderTable = createOrderTable(false, 4);

        orderTableRepository.save(orderTable);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTableRequest))
                .isInstanceOf(GuestLessThanZeroException.class);
    }


    @Test
    @DisplayName("주문 테이블이 공석이라면 인원수 변경이 가능하다")
    void orderTablePossibleChangeNumberOgGuestTest() {
        final OrderTable orderTableRequest = createOrderTable(7);
        final OrderTable orderTable = createOrderTable(true, 4);

        orderTableRepository.save(orderTable);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTableRequest))
                .isInstanceOf(OrderTabmeIsEmptyException.class);
    }

    /*
     * 주문 테이블의 인원 변경
     * */

    @Test
    @DisplayName("주문 테이블을 전체 조회할 수 있다.")
    void orderTableFindAllTest() {
        orderTableRepository.save(createOrderTable());
        orderTableRepository.save(createOrderTable());
        orderTableRepository.save(createOrderTable());

        List<OrderTable> orderTables = orderTableService.findAll();

        Assertions.assertThat(orderTables).hasSize(3);
    }

    private Order createOrder(final OrderStatus orderStatus, final OrderTable orderTable) {
        final Order order = new Order();
        order.setStatus(orderStatus);
        order.setOrderTable(orderTable);

        return order;
    }


    private OrderTable createOrderTable() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        return orderTable;
    }

    private OrderTable createOrderTable(final String name) {
        final OrderTable orderTable = createOrderTable();
        orderTable.setName(name);

        return orderTable;
    }

    private OrderTable createOrderTable(final boolean isEmpty) {
        final OrderTable orderTable = createOrderTable();
        orderTable.setEmpty(isEmpty);

        return orderTable;
    }


    private OrderTable createOrderTable(final int numberOfGuests) {
        final OrderTable orderTable = createOrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);

        return orderTable;
    }

    private OrderTable createOrderTable(final boolean isEmpty, final int numberOfGuests) {
        final OrderTable orderTable = createOrderTable(isEmpty);
        orderTable.setNumberOfGuests(numberOfGuests);

        return orderTable;
    }
}