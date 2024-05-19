package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.testfixture.InMemoryOrderRepository;
import kitchenpos.testfixture.InMemoryOrderTableRepository;
import kitchenpos.testfixture.OrderTableTestFixture;
import kitchenpos.testfixture.OrderTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InMemoryOrderTableRepository();
        orderRepository = new InMemoryOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Nested
    @DisplayName("주문 테이블 생성")
    class create {
        @Test
        @DisplayName("주문 테이블 생성 성공")
        void create() {

            //given
            OrderTable request = OrderTableTestFixture.createOrderTableRequest();

            //when
            OrderTable response = orderTableService.create(request);

            //then
            assertThat(response.getId()).isNotNull();
            assertThat(response.getNumberOfGuests()).isZero();
            assertThat(response.isOccupied()).isFalse();
            assertThat(response.getName()).isEqualTo(request.getName());
        }

        @Test
        @DisplayName("주문테이블의 이름은 공백일 수 없다.")
        void canNotEmptyName() {
            //given
            OrderTable request = OrderTableTestFixture.createOrderTableRequest("", true, 99);

            //when then
            assertThatThrownBy(() -> orderTableService.create(request))
                    .isExactlyInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("테이블 사용중")
    class sit {

        @Test
        @DisplayName("테이블 사용중으로 변경 성공")
        void sit() {
            //given
            OrderTable request = OrderTableTestFixture.createOrderTable("2번", false, 10);
            orderTableRepository.save(request);

            //when
            OrderTable response = orderTableService.sit(request.getId());

            //then
            assertThat(response.isOccupied()).isTrue();

        }

        @Test
        @DisplayName("유효하지 않은 테이블의 상태를 변경할 수 없다.")
        void canNotChangeOccupiedNoTable() {
            //given
            OrderTable request = OrderTableTestFixture.createOrderTable("2번", true, 10);

            //when then
            assertThatThrownBy(() -> orderTableService.sit(request.getId()))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class clear{
        @Test
        @DisplayName("테이블 비어있는 상태로 변경 성공")
        void clear() {
            //given
            OrderTable request = OrderTableTestFixture.createOrderTable("2번", true, 10);
            orderTableRepository.save(request);

            //when
            OrderTable response = orderTableService.clear(request.getId());

            //then
            assertThat(response.isOccupied()).isFalse();
            assertThat(response.getNumberOfGuests()).isZero();
        }

        @Test
        @DisplayName("유효하지 않은 테이블의 상태를 변경할 수 없다.")
        void canNotChangeOccupiedNoTable() {
            //given
            OrderTable request = OrderTableTestFixture.createOrderTable("2번", true, 10);

            //when then
            assertThatThrownBy(() -> orderTableService.clear(request.getId()))
                    .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문상태가 종료되지 않은 주문이 있다면 테이블을 비울 수 없다.")
        void canNotClearWhenHaveOrder() {
            //given
            OrderTable request = OrderTableTestFixture.createOrderTable("2번", true, 10);
            orderTableRepository.save(request);
            Order order = OrderTestFixture.createOrderRequest(
                    OrderType.EAT_IN, OrderStatus.SERVED, LocalDateTime.now(),
                    List.of(new OrderLineItem()), request
            );
            orderRepository.save(order);

            //when then
            UUID tableId = request.getId();
            assertThatThrownBy(() -> orderTableService.clear(tableId))
                    .isExactlyInstanceOf(IllegalStateException.class);

        }
    }

    @Nested
    @DisplayName("손님 수 변경")
    class NumberOfGuests {

        OrderTable orderTable;

        @BeforeEach
        void setUp(){
            orderTable = OrderTableTestFixture.createOrderTable("3번", true, 10);
            orderTableRepository.save(orderTable);
        }

        @Test
        @DisplayName("손님 수 변경 성공")
        void changeNumberOfGuests() {

            //given
            orderTable.setNumberOfGuests(5);

            //when
            OrderTable response = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

            //then
            assertThat(orderTable.getNumberOfGuests()).isEqualTo(response.getNumberOfGuests());

        }

        @Test
        @DisplayName("손님 수는 음수일 수 없다.")
        void canNotInputMinus() {

            //given
            orderTable.setNumberOfGuests(-1);

            //when then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                    .isExactlyInstanceOf(IllegalArgumentException.class);

        }

        @Test
        @DisplayName("유효하지 않는 테이블에서 손님수를 변경할 수 없다.")
        void canNotChangeNoOrderTable() {

            //given
            orderTable.setNumberOfGuests(5);

            //when then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), orderTable))
                    .isExactlyInstanceOf(NoSuchElementException.class);

        }

        @Test
        @DisplayName("비어 있는 테이블에서 손님수를 변경할 수 없다.")
        void canNotChangeNotUsedOrderTable() {

            //given
            OrderTable emptyTable = OrderTableTestFixture.createOrderTable("3번", false, 0);
            orderTableRepository.save(emptyTable);

            emptyTable.setNumberOfGuests(3);

            //when then
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(emptyTable.getId(), emptyTable))
                    .isExactlyInstanceOf(IllegalStateException.class);

        }
    }

    @Nested
    @DisplayName("주문 테이블 조회")
    class find {
        @Test
        @DisplayName("모든 주문 테이블 조회")
        void findAll() {

            //given
            OrderTable orderTable1 = OrderTableTestFixture.createOrderTable(
                    "1번", false, 0
            );
            OrderTable orderTable2 = OrderTableTestFixture.createOrderTable(
                    "2번", true, 2
            );
            orderTableRepository.save(orderTable1);
            orderTableRepository.save(orderTable2);

            //when
            List<OrderTable> response = orderTableService.findAll();

            //then
            assertThat(response).hasSize(2);
            assertThat(response)
                    .filteredOn(OrderTable::getId, orderTable1.getId())
                    .containsExactly(orderTable1);
            assertThat(response)
                    .filteredOn(OrderTable::getId, orderTable2.getId())
                    .containsExactly(orderTable2);
        }
    }
}