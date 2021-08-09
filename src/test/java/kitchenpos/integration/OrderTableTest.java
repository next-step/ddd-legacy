package kitchenpos.integration;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.*;
import kitchenpos.integration.annotation.TestAndRollback;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderTableTest extends IntegrationTestRunner {

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @DisplayName("주문 테이블 생성 ( 주문 테이블의 이름은 `null` 일 수 없다. )")
    @TestAndRollback
    public void create_with_null_name() {
        //given
        final OrderTable request = new OrderTable();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.create(request));
    }

    @DisplayName("주문 테이블 생성 ( 주문 테이블의 이름은 `공백` 일 수 없다. )")
    @TestAndRollback
    public void create_with_empty_name() {
        //given
        final String emptyName = "";
        final OrderTable request = new OrderTable();
        request.setName(emptyName);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.create(request));
    }

    @DisplayName("주문 테이블 생성")
    @TestAndRollback
    public void create() {
        //given
        final String orderTableName = "1번 테이블";
        final OrderTable request = new OrderTable();
        request.setName(orderTableName);

        //when
        final OrderTable orderTable = orderTableService.create(request);

        //then
        assertAll(
                () -> assertThat(orderTable.getName()).isEqualTo(orderTableName),
                () -> assertThat(orderTable.getId()).isNotNull(),
                () -> assertThat(orderTable.getNumberOfGuests()).isZero(),
                () -> assertThat(orderTable.isEmpty()).isTrue()
        );
    }

    @DisplayName("주문 테이블 착석 ( 요청 주문 테이블이 영속화 되어 있어야 한다. )")
    @TestAndRollback
    public void sit_with_not_persisted_order_table() {
        //given
        final UUID orderTableUuid = UUID.randomUUID();

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.sit(orderTableUuid));

    }

    @DisplayName("주문 테이블 착석")
    @TestAndRollback
    public void sit() {
        //given
        final UUID orderTableUuid = UUID.randomUUID();
        final String orderTableName = "1번 테이블";
        final OrderTable orderTable = new OrderTable();
        orderTable.setName(orderTableName);
        orderTable.setId(orderTableUuid);

        orderTableRepository.save(orderTable);

        //when
        final OrderTable sitTable = orderTableService.sit(orderTableUuid);

        //then
        assertThat(sitTable.isEmpty()).isFalse();
    }

    @DisplayName("주문 테이블 Clear ( 요청 주문 테이블이 영속화 되어 있어야 한다. )")
    @TestAndRollback
    public void clear_with_not_persisted_order_table() {
        //given
        final UUID orderTableUuid = UUID.randomUUID();

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.clear(orderTableUuid));
    }

    @DisplayName("주문 테이블 Clear ( 요청 주문 테이블의 주문 상태가 `완료` 이어야 한다. )")
    @TestAndRollback
    public void clear_with_order_status_completed() {
        //given
        final UUID orderTableUuid = UUID.randomUUID();
        final String orderTableName = "1번 테이블";
        final OrderTable orderTable = new OrderTable();
        orderTable.setName(orderTableName);
        orderTable.setId(orderTableUuid);

        orderTableRepository.save(orderTable);

        final Order order = getFixtureOrder(orderTableUuid, orderTable, OrderStatus.SERVED);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.clear(orderTableUuid));
    }

    @DisplayName("주문 테이블 Clear")
    @TestAndRollback
    public void clear() {
        //given
        final UUID orderTableUuid = UUID.randomUUID();
        final String orderTableName = "1번 테이블";
        final OrderTable orderTable = new OrderTable();
        orderTable.setName(orderTableName);
        orderTable.setId(orderTableUuid);

        orderTableRepository.save(orderTable);

        final Order order = getFixtureOrder(orderTableUuid, orderTable, OrderStatus.COMPLETED);

        orderRepository.save(order);

        //when
        final OrderTable clearTable = orderTableService.clear(orderTableUuid);

        //then
        assertAll(
                () -> assertThat(clearTable.getNumberOfGuests()).isZero(),
                () -> assertThat(clearTable.isEmpty()).isTrue()

        );
    }


    @DisplayName("주문 테이블 인원수 변경 ( 요청 인원수는 `0`보다 작을 수 없다. )")
    @TestAndRollback
    public void change_number_of_guest_with_minus_guest() {
        //given
        final int minusNumber = -1;
        final UUID orderTableUuid = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(orderTableUuid);
        orderTable.setName("테이블 1번");
        orderTable.setNumberOfGuests(minusNumber);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableUuid, orderTable));
    }

    @DisplayName("주문 테이블 인원수 변경 ( 요청 주문 테이블이 영속화 되어 있어야 한다. )")
    @TestAndRollback
    public void change_number_of_guest_with_not_persisted_order_table() {
        //given
        final UUID orderTableUuid = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(orderTableUuid);
        orderTable.setName("테이블 1번");
        orderTable.setNumberOfGuests(3);

        final OrderTable request = new OrderTable();
        request.setNumberOfGuests(5);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableUuid, request));
    }

    @DisplayName("주문 테이블 인원수 변경 ( 요청 주문 테이블은 `이용중` 이어야 한다. )")
    @TestAndRollback
    public void change_number_of_guest_with_empty() {
        //given
        final UUID orderTableUuid = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(orderTableUuid);
        orderTable.setName("테이블 1번");
        orderTable.setNumberOfGuests(3);
        orderTable.setEmpty(true);

        orderTableRepository.save(orderTable);

        final OrderTable request = new OrderTable();
        request.setNumberOfGuests(5);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableUuid, request));
    }

    @DisplayName("주문 테이블 인원수 변경")
    @TestAndRollback
    public void change_number_of_guest() {
        //given
        final UUID orderTableUuid = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(orderTableUuid);
        orderTable.setName("테이블 1번");
        orderTable.setNumberOfGuests(1);
        orderTable.setEmpty(false);

        orderTableRepository.save(orderTable);

        final int changeNumber = 5;
        final OrderTable request = new OrderTable();
        request.setNumberOfGuests(changeNumber);

        //when
        final OrderTable changedTable = orderTableService.changeNumberOfGuests(orderTableUuid, request);

        //then
        assertThat(changedTable.getNumberOfGuests()).isEqualTo(changeNumber);
    }

    @DisplayName("모든 주문 테이블 조회")
    @TestAndRollback
    public void findAll() {
        //given
        final UUID orderTableUuid_1 = UUID.randomUUID();
        final OrderTable orderTable_1 = new OrderTable();
        orderTable_1.setId(orderTableUuid_1);
        orderTable_1.setName("테이블 1번");
        orderTable_1.setNumberOfGuests(1);
        orderTable_1.setEmpty(false);

        final UUID orderTableUuid_2 = UUID.randomUUID();
        final OrderTable orderTable_2 = new OrderTable();
        orderTable_2.setId(orderTableUuid_2);
        orderTable_2.setName("테이블 2번");
        orderTable_2.setNumberOfGuests(2);
        orderTable_2.setEmpty(false);

        final UUID orderTableUuid_3 = UUID.randomUUID();
        final OrderTable orderTable_3 = new OrderTable();
        orderTable_3.setId(orderTableUuid_3);
        orderTable_3.setName("테이블 3번");
        orderTable_3.setNumberOfGuests(3);
        orderTable_3.setEmpty(false);

        orderTableRepository.saveAll(List.of(orderTable_1, orderTable_2, orderTable_3));

        //when
        final List<OrderTable> orderTables = orderTableService.findAll();

        //then
        assertAll(
                () -> assertThat(orderTables.size()).isEqualTo(3),
                () -> assertThat(orderTables.get(0)).isEqualTo(orderTable_1),
                () -> assertThat(orderTables.get(1)).isEqualTo(orderTable_2),
                () -> assertThat(orderTables.get(2)).isEqualTo(orderTable_3)
        );

    }

    private Order getFixtureOrder(final UUID orderTableUuid, final OrderTable orderTable, final OrderStatus completed) {
        final UUID orderUuid = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTable(orderTable);
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(orderTableUuid);
        order.setId(orderUuid);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(completed);
        return order;
    }
}
