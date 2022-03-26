package kitchenpos;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.TestFixtures.createOrderTable;
import static kitchenpos.TestFixtures.createOrderTableRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTableServiceTest {


    private final OrderTableRepository orderTableRepository = new FakeOrderTableRepository();
    private final OrderRepository orderRepository = new FakeOrderRepository();


    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }


    @DisplayName("주문 테이블을 등록할 수 있다.")
    @Test
    void create() {
        // given
        OrderTable orderTableRequest = createOrderTableRequest("8번");


        // when
        OrderTable result = orderTableService.create(orderTableRequest);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(orderTableRequest.getName());

    }

    @DisplayName("주문 테이블 등록 시 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void createWithoutName(String name) {
        // given
        OrderTable orderTableRequest = createOrderTableRequest(name);


        // when - then
        assertThatThrownBy(() -> orderTableService.create(orderTableRequest))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("빈 테이블을 설정할 수 있다.")
    @Test
    void emptyTable() {

        // given
        OrderTable orderTable = createOrderTable("8번");
        orderTableRepository.save(orderTable);

        // when
        OrderTable result = orderTableService.clear(orderTable.getId());

        // then
        assertThat(result.isEmpty()).isTrue();
    }

    @DisplayName("상태를 변경할 때 변경할 테이블은 미리 등록된 상태여야 한다.")
    @Test
    void emptyTableNotRegistered() {

        // given
        OrderTable orderTable = createOrderTable("8번");

        // when - then
        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("빈 테이블을 설정할 테이블은 주문 상태가 `완료` 상태여야만 한다.")
    @ValueSource(strings = {"DELIVERED", "ACCEPTED", "DELIVERING","SERVED", "WAITING"})
    @ParameterizedTest
    void emptyTableOnlyCompleted(OrderStatus orderStatus) {

        // given
        OrderTable orderTable = createOrderTable("8번");
        orderTableRepository.save(orderTable);
        Order order = createOrder(orderStatus, orderTable);
        orderRepository.save(order);

        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }


    private Order createOrder(OrderStatus orderStatus, OrderTable orderTable) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(orderStatus);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        return order;
    }

    @DisplayName("빈 테이블을 해지할 수 있다.")
    @Test
    void notEmptyTable() {


        // given
        OrderTable orderTable = createOrderTable("8번");
        orderTableRepository.save(orderTable);

        // when
        OrderTable result = orderTableService.sit(orderTable.getId());


        assertThat(result.isEmpty()).isFalse();

    }

    @DisplayName("빈 테이블을 해지할 때 테이블은 미리 등록된 상태여야 한다.")
    @Test
    void notEmptyTableNotRegistered() {
        // given
        OrderTable orderTable = createOrderTable("8번");

        // when - then
        assertThatThrownBy(() -> orderTableService.sit(orderTable.getId()))
                .isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("방문할 손님 수를 입력할 수 있다.")
    @Test
    void changeNumberOfGuests() {

        // given
        OrderTable orderTable = createOrderTable("8번");
        orderTableRepository.save(orderTable);

        OrderTable orderTableRequest = createOrderTableRequest("8번");
        orderTableRequest.setNumberOfGuests(3);

        // when
        OrderTable result = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTableRequest);

        // then
        assertThat(result.getNumberOfGuests()).isEqualTo(3);
    }

    @DisplayName("방문할 손님 수는 0명 미만으로 입력할 수 없다.")
    @Test
    void changeNumberOfGuestsNotNegative() {

        // given
        OrderTable orderTable = createOrderTable("8번");
        orderTableRepository.save(orderTable);

        OrderTable orderTableRequest = createOrderTableRequest("8번");
        orderTableRequest.setNumberOfGuests(-3);

        // when - then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTableRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("손님 수를 입력할 테이블은 미리 등록된 상태여야 한다.")
    @Test
    void changeNumberOfGuestsTableRegistered() {

        // given
        OrderTable orderTable = createOrderTable("8번");

        OrderTable orderTableRequest = createOrderTableRequest("8번");
        orderTableRequest.setNumberOfGuests(3);

        // when - then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTableRequest))
                .isInstanceOf(NoSuchElementException.class);

    }

    @DisplayName("빈 테이블은 방문한 손님 수를 입력할 수 없다.")
    @Test
    void changeNumberOfGuestsNotEmpty() {

        // given
        OrderTable orderTable = createOrderTable("8번");
        orderTable.setEmpty(true);
        orderTableRepository.save(orderTable);

        OrderTable orderTableRequest = createOrderTableRequest("8번");
        orderTableRequest.setNumberOfGuests(3);

        // when - then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTableRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 테이블의 목록을 조회할 수 있다.")
    @Test
    void findAllOrderTables() {
        // given
        OrderTable orderTable1 = createOrderTable("8번");
        orderTableRepository.save(orderTable1);
        OrderTable orderTable2 = createOrderTable("9번");
        orderTableRepository.save(orderTable2);


        // when
        List<OrderTable> results = orderTableService.findAll();

        // then
        assertThat(results.size()).isEqualTo(2);
    }


}