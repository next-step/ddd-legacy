package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.exception.GuestLessThanZeroException;
import kitchenpos.exception.OrderTabmeIsEmptyException;
import kitchenpos.exception.PriceLessThanZeroException;
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

import static org.junit.jupiter.api.Assertions.*;

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

        OrderTable orderTableRequest = new OrderTable();
        orderTableRequest.setName("1번 테이블");

        OrderTable actual = orderTableService.create(orderTableRequest);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(actual.getId()).isNotNull(),
                () -> Assertions.assertThat(actual.getName()).isEqualTo(orderTableRequest.getName())
        );
    }
    @Test
    @DisplayName("주문 테이블의 이름은 필수로 있어야 한다.")
    void createOrderTableNameTest() {

        OrderTable orderTableRequest = new OrderTable();

        AssertionsForClassTypes.assertThatThrownBy(() -> orderTableService.create(orderTableRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /*
     * 주문 테이블의 착석 상태를 변경한다.
     * */

    @Test
    @DisplayName("주문 테이블을 착석 상태(false)로 변경")
    void orderTableSitTest() {

        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setEmpty(true);
        orderTableRepository.save(orderTable);

        OrderTable actual = orderTableService.sit(orderTable.getId());

        Assertions.assertThat(actual.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("주문 테이블을 착성 상태로 변경시 주문 테이블의 정보가 있어야 한다")
    void orderTableSitNotFoundTest() {

        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());

        AssertionsForClassTypes.assertThatThrownBy(() -> orderTableService.sit(orderTable.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("주문 테이블을 공석 상태(true)로 변경")
    void orderTableClearTest() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setNumberOfGuests(4);
        orderTable.setEmpty(false);
        orderTableRepository.save(orderTable);

        Order order = new Order();
        order.setOrderTable(orderTable);
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        OrderTable actual = orderTableService.clear(orderTable.getId());

        org.junit.jupiter.api.Assertions.assertAll(
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

        OrderTable orderTableRequest = new OrderTable();
        orderTableRequest.setNumberOfGuests(-1);

        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setNumberOfGuests(4);
        orderTable.setEmpty(false);
        orderTableRepository.save(orderTable);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTableRequest))
                .isInstanceOf(GuestLessThanZeroException.class);
    }

    @Test
    @DisplayName("주문 테이블이 공석이라면 인원수 변경이 가능하다")
    void orderTableChangeNumberOfGuests() {

        OrderTable orderTableRequest = new OrderTable();
        orderTableRequest.setNumberOfGuests(7);

        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setNumberOfGuests(4);
        orderTable.setEmpty(true);
        orderTableRepository.save(orderTable);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTableRequest))
                .isInstanceOf(OrderTabmeIsEmptyException.class);
    }

    /*
     * 주문 테이블의 인원 변경
     * */
    void orderTableFindAllTest() {
        OrderTable orderTable1 = new OrderTable();
        orderTable1.setId(UUID.randomUUID());
        orderTableRepository.save(orderTable1);

        OrderTable orderTable2 = new OrderTable();
        orderTable2.setId(UUID.randomUUID());
        orderTableRepository.save(orderTable2);

        OrderTable orderTable3 = new OrderTable();
        orderTable3.setId(UUID.randomUUID());
        orderTableRepository.save(orderTable3);

        List<OrderTable> orderTables = orderTableService.findAll();

        Assertions.assertThat(orderTables).hasSize(3);
    }
}