package kitchenpos.application;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderTableServiceTest {
    private OrderTableService orderTableService;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("테이블을 등록할 수 있다.")
    @Test
    void create() {
        final OrderTable request = createOrderTable("table1");

        final OrderTable actual = orderTableService.create(request);

        assertAll(
                () -> assertThat(actual.isEmpty()).isTrue(),
                () -> assertThat(actual.getNumberOfGuests()).isZero()
        );
    }

    @DisplayName("테이블 등록시 테이블 이름이 존재해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create_with_empty_name(String name) {
        final OrderTable request = createOrderTable(name);

        assertThatCode(
                () -> orderTableService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 목록을 조회할 수 있다.")
    @Test
    void find_all() {
        final OrderTable request1 = createOrderTable("table1");
        final OrderTable request2 = createOrderTable("table1");
        final OrderTable orderTable1 = orderTableService.create(request1);
        final OrderTable orderTable2 = orderTableService.create(request2);

        final List<OrderTable> actual = orderTableService.findAll();

        assertThat(actual).containsAll(Arrays.asList(orderTable1, orderTable2));
    }

    @TestFactory
    Collection<DynamicTest> 앉은상태_상태_변경() {
        final OrderTable request = createOrderTable("table1");
        final OrderTable givenOrder = orderTableService.create(request);

        return Arrays.asList(dynamicTest("최초의 테이블은 비어있는 상태가 활성화이다.", () -> {
                    assertThat(givenOrder.isEmpty()).isTrue();
                }),
                dynamicTest("승인 상태로 변경할 수 있다.", () -> {
                    final OrderTable changed = orderTableService.sit(givenOrder.getId());
                    assertThat(changed.isEmpty()).isFalse();
                }));
    }

    @DisplayName("테이블 정보가 존재해야한다.")
    @Test
    void sit_with_not_found_order_table() {
        final String notFoundUUID = "06fe3514-a8a6-48ed-85e6-e7296d0e1000";

        assertThatCode(() -> orderTableService.sit(UUID.fromString(notFoundUUID)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("테이블을 해제할 수 있다.")
    @Test
    void clear() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(5);
        orderTable.setName("sit table");
        final OrderTable givenOrderTable = orderTableRepository.save(orderTable);

        final OrderTable actual = orderTableService.clear(givenOrderTable.getId());

        assertAll(
                () -> assertThat(actual.isEmpty()).isTrue(),
                () -> assertThat(actual.getNumberOfGuests()).isZero()
        );
    }

    @DisplayName("테이블 정보가 존재해야한다.")
    @Test
    void clear_with_not_found_order_table() {
        final String notFoundUUID = "06fe3514-a8a6-48ed-85e6-e7296d0e1000";

        assertThatCode(() -> orderTableService.clear(UUID.fromString(notFoundUUID)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문완료 상태가 아닌 테이블은 해제시 예외를 던진다.")
    @Test
    void clear_with_exist_order_table_and_status_not_order_complete() {
        final OrderTable givenOrderTable = orderTableRepository.save(createNotEmptyOrderTable());
        final Order orderRequest = createOrderWithNotOrderComplete(givenOrderTable);
        orderRepository.save(orderRequest);

        assertThatCode(() -> orderTableService.clear(givenOrderTable.getId())
        ).isInstanceOf(IllegalStateException.class);
    }

    @TestFactory
    Collection<DynamicTest> 손님_수_변경() {
        final UUID givenUUID = UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1000");

        return Arrays.asList(dynamicTest("점유된 테이블의 손님수", () -> {
                    OrderTable orderTable = new OrderTable();
                    orderTable.setId(givenUUID);
                    orderTable.setEmpty(false);
                    orderTable.setNumberOfGuests(5);
                    orderTable.setName("sit table");
                    final OrderTable actual = orderTableRepository.save(orderTable);

                    assertThat(actual.getNumberOfGuests()).isEqualTo(5);
                }),
                dynamicTest("손님 수를 변경할 수 있다.", () -> {
                    OrderTable changeRequest = new OrderTable();
                    changeRequest.setNumberOfGuests(2);

                    final OrderTable actual = orderTableService.changeNumberOfGuests(givenUUID, changeRequest);
                    assertThat(actual.getNumberOfGuests()).isEqualTo(2);
                }));
    }

    @DisplayName("테이블 정보가 존재해야한다.")
    @Test
    void change_number_of_guests_with_no_table_info() {
        OrderTable changeRequest = new OrderTable();
        changeRequest.setNumberOfGuests(2);
        final String notFoundUUID = "06fe3514-a8a6-48ed-85e6-e7296d0e1000";

        assertThatCode(() -> orderTableService.changeNumberOfGuests(UUID.fromString(notFoundUUID), changeRequest))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("변경하려는 손님의 수는 0보다 작을 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -10})
    void change_number_of_guests_negative_numbers(int numberOfGuest) {
        OrderTable changeRequest = new OrderTable();
        changeRequest.setNumberOfGuests(numberOfGuest);

        assertThatCode(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), changeRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블이 비어있는 상태에서는 변경할 수 없다.")
    @Test
    void change_number_of_guests_with_empty_table() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setEmpty(true);
        orderTable.setNumberOfGuests(5);
        orderTable.setName("sit table");
        final OrderTable givenOrderTable = orderTableRepository.save(orderTable);
        OrderTable changeRequest = new OrderTable();
        changeRequest.setNumberOfGuests(2);

        assertThatCode(() -> orderTableService.changeNumberOfGuests(givenOrderTable.getId(), changeRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    private OrderTable createOrderTable(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        return orderTable;
    }

    private OrderTable createNotEmptyOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(5);
        orderTable.setName("sit table");
        return orderTable;
    }

    private Order createOrderWithNotOrderComplete(OrderTable givenOrderTable) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderTable(givenOrderTable);
        order.setOrderTableId(givenOrderTable.getId());
        order.setOrderDateTime(LocalDateTime.now());
        return order;
    }
}
