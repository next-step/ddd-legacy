package kitchenpos.unit.application;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.*;
import kitchenpos.domain.Order;
import kitchenpos.testdouble.OrderStubRepository;
import kitchenpos.testdouble.OrderTableStubRepository;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTableServiceTest {

    private OrderTableService orderTableService;
    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderTableRepository = new OrderTableStubRepository();
        orderRepository = new OrderStubRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Nested
    @DisplayName("주문 테이블 생성 테스트")
    class CreateTest {

        @DisplayName("이름 없는 주문 테이블은 생성할 수 없다.")
        @Test
        void createWithoutName() {
            // Arrange
            OrderTable orderTable = new OrderTable();

            // Act
            // Assert
            assertThatThrownBy(() -> orderTableService.create(orderTable)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 테이블 생성 성공.")
        @Test
        void create() {
            // Arrange
            OrderTable orderTable = new OrderTable();
            orderTable.setName("order table name");

            // Act
            OrderTable result = orderTableService.create(orderTable);

            // Assert
            assertAll(
                    () -> assertThat(result.getId()).isNotNull(),
                    () -> assertThat(result.getName()).isEqualTo("order table name"),
                    () -> assertThat(result.getNumberOfGuests()).isZero(),
                    () -> assertThat(result.isEmpty()).isTrue()
            );
        }
    }

    @DisplayName("주문 테이블 좌석 상태 변경 테스트")
    @Nested
    class SitTest {
        @DisplayName("등록된 주문 테이블의 좌석 상태를 변경 한다.")
        @Test
        void sit() {
            // Arrange
            OrderTable orderTable = 주문_테이블_생성_요청();

            // Act
            orderTableService.sit(orderTable.getId());

            // Assert
            assertThat(orderTable.isEmpty()).isFalse();
        }
    }

    @DisplayName("테이블 초기화 테스트")
    @Nested
    class ClearTest {

        @DisplayName("등록된 주문 테이블의 주문 상태가 완료가 아니라면 좌석을 초기화 할 수 없다.")
        @Test
        void cannotClear() {
            // Arrange
            OrderTable orderTable = 주문_테이블_생성_요청();
            Order order = 주문_생성_요청(orderTable, OrderStatus.ACCEPTED);

            // Act
            // Assert
            assertThatThrownBy(() -> orderTableService.clear(orderTable.getId())).isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("등록된 주문 테이블의 주문 상태가 완료일떄 주문 테이블의 좌석을 초기화 할 수 있다.")
        @Test
        void clear() {
            // Arrange
            OrderTable orderTable = 주문_테이블_생성_요청();
            Order order = 주문_생성_요청(orderTable, OrderStatus.COMPLETED);

            // Act
            OrderTable result = orderTableService.clear(orderTable.getId());

            // Assert
            assertAll(
                    () -> assertThat(result.isEmpty()).isTrue(),
                    () -> assertThat(result.getNumberOfGuests()).isZero()
            );
        }
    }

    @Nested
    @DisplayName("주문 테이블 인원 변경 테스트")
    class ChangeNumberOfGuestsTest {

        @DisplayName("변경 하려는 인원 수가 0 이하라면 변경할 수 없다.")
        @Test
        void numberOfGuestsUnderZero() {
            // Arrange
            UUID id = 주문_테이블_생성_요청().getId();
            OrderTable request = new OrderTable();
            request.setNumberOfGuests(-1);

            // Act
            // Assert
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(id, request)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("변경 하려는 테이블이 비어있다면 변경할 수 없다.")
        @Test
        void changeTableEmpty() {
            // Arrange
            UUID id = 주문_테이블_생성_요청().getId();
            OrderTable request = new OrderTable();
            request.setNumberOfGuests(1);

            // Act
            // Assert
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(id, request)).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("등록된 모든 테이블을 조회 한다.")
    @Test
    void findAllRegisteredTable() {
        // Arrange
        OrderTable table1 = 주문_테이블_생성_요청();
        OrderTable table2 = 주문_테이블_생성_요청();

        // Act
        List<OrderTable> orderTables = orderTableService.findAll();

        // Assert
        assertThat(orderTables).contains(table1, table2);
    }

    private Order 주문_생성_요청(OrderTable orderTable, OrderStatus status) {
        Order order = new Order();
        order.setOrderTable(orderTable);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    private OrderTable 주문_테이블_생성_요청() {
        OrderTable orderTable = createOrderTable();
        return orderTableService.create(orderTable);
    }

    private OrderTable createOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName("order table name");
        return orderTable;
    }
}