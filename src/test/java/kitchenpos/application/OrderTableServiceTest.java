package kitchenpos.application;

import jakarta.transaction.Transactional;
import kitchenpos.domain.*;
import kitchenpos.domain.Order;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootTest
class OrderTableServiceTest {
    @Autowired
    private OrderTableRepository orderTableRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderTableService orderTableService;

    private OrderTable createOrderTable(String name, boolean occupied, int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTableRepository.save(orderTable);
    }

    private Order createOrder(OrderTable orderTable, OrderStatus status) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setStatus(status);
        order.setOrderDateTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Nested
    @DisplayName("주문 테이블 생성")
    class CreateOrderTableTest {

        @Test
        @DisplayName("주문 테이블을 생성할 수 있다.")
        void create() {
            OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setName("1번");

            OrderTable result = orderTableService.create(orderTable);

            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getName()).isEqualTo("1번");
            Assertions.assertThat(result.isOccupied()).isFalse();
            Assertions.assertThat(result.getNumberOfGuests()).isZero();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("주문 테이블 생성 시, 테이블 명이 없으면 IllegalArgumentException 예외 발생")
        void cannotCreateOrderTableWithInvalidName(String invalidName) {
            OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setName(invalidName);

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderTableService.create(orderTable));
        }
    }

    @Nested
    @DisplayName("주문 테이블 상태 변경")
    class ChangeOrderTableStatusTest {

        @Test
        @DisplayName("주문 테이블에 앉을 수 있다.")
        void sit() {
            OrderTable orderTable = createOrderTable("1번", false, 0);

            OrderTable result = orderTableService.sit(orderTable.getId());

            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.isOccupied()).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 테이블에 앉으려 하면 NoSuchElementException 예외 발생")
        void cannotSitIfTableDoesNotExist() {
            UUID nonExistId = UUID.randomUUID();

            Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderTableService.sit(nonExistId));
        }

        @Test
        @DisplayName("주문 테이블을 정리할 수 있다.")
        void clear() {
            OrderTable orderTable = createOrderTable("1번", true, 4);

            orderTableService.clear(orderTable.getId());

            OrderTable result = orderTableRepository.findById(orderTable.getId()).orElseThrow();
            Assertions.assertThat(result.isOccupied()).isFalse();
            Assertions.assertThat(result.getNumberOfGuests()).isZero();
        }

        @Test
        @DisplayName("존재하지 않는 테이블을 정리하려 하면 NoSuchElementException 예외 발생")
        void cannotClearIfTableDoesNotExist() {
            UUID nonExistId = UUID.randomUUID();

            Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderTableService.clear(nonExistId));
        }

        @Test
        @DisplayName("완료되지 않은 주문이 있으면 테이블을 정리할 수 없다.")
        void cannotClearTableIfOrderIsNotCompleted() {
            OrderTable orderTable = createOrderTable("1번", true, 4);
            Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setOrderTable(orderTable);
            order.setOrderTableId(orderTable.getId());
            order.setStatus(OrderStatus.WAITING);
            order.setType(OrderType.EAT_IN);
            order.setOrderDateTime(LocalDateTime.now());
            orderRepository.save(order);

            Assertions.assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderTableService.clear(orderTable.getId()));
        }

    }

    @Nested
    @DisplayName("주문 테이블 인원 변경")
    class ChangeNumberOfGuestsTest {

        @Test
        @DisplayName("테이블 인원 수를 변경할 수 있다.")
        void changeNumberOfGuests() {
            OrderTable orderTable = createOrderTable("1번", true, 3);

            OrderTable changedTable = new OrderTable();
            changedTable.setNumberOfGuests(4);

            OrderTable result = orderTableService.changeNumberOfGuests(orderTable.getId(), changedTable);

            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getNumberOfGuests()).isEqualTo(4);
        }

        @Test
        @DisplayName("인원 수가 0 미만이면 IllegalArgumentException 예외 발생")
        void cannotChangeNumberOfGuestsToNegative() {
            OrderTable orderTable = createOrderTable("1번", true, 3);

            OrderTable changedTable = new OrderTable();
            changedTable.setNumberOfGuests(-1);

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), changedTable));
        }

        @Test
        @DisplayName("존재하지 않는 테이블의 인원 수를 변경하면 NoSuchElementException 예외 발생")
        void cannotChangeGuestsIfTableDoesNotExist() {
            OrderTable changedTable = new OrderTable();
            changedTable.setNumberOfGuests(3);

            Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), changedTable));
        }

        @Test
        @DisplayName("사용 중이지 않은 테이블의 인원 수를 변경하면 IllegalStateException 예외 발생")
        void cannotChangeGuestsIfTableIsNotOccupied() {
            OrderTable orderTable = createOrderTable("1번", false, 3);

            OrderTable changedTable = new OrderTable();
            changedTable.setNumberOfGuests(2);

            Assertions.assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), changedTable));
        }
    }

    @Nested
    @DisplayName("주문 테이블 조회")
    class FindOrderTableTest {

        @Test
        @DisplayName("모든 주문 테이블을 조회할 수 있다.")
        void findAll() {
            OrderTable orderTable1 = createOrderTable("1번", true, 4);
            OrderTable orderTable2 = createOrderTable("2번", true, 2);

            List<OrderTable> result = orderTableService.findAll();

            Assertions.assertThat(result).hasSize(2);
            Assertions.assertThat(result).extracting(OrderTable::getName)
                    .containsExactlyInAnyOrder("1번", "2번");
        }
    }
}
