package kitchenpos;

import kitchenpos.application.*;
import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kitchenpos.domain.OrderStatus.COMPLETED;
import static kitchenpos.domain.OrderStatus.SERVED;
import static kitchenpos.fixture.OrderFixture.createOrder;
import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName(value = " OrderTable 테스트")
public class OrderTableTest {

    private static final OrderStatus ORDER_STATUS_제공완료 = SERVED;
    private static final OrderStatus ORDER_STATUS_주문완료 = COMPLETED;
    private static final String ORDER_TABLE_DEFAULT_NAME = "1번";


    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private FakeKitchenridersClient fakeKitchenridersClient;
    private OrderRepository orderRepository;
    private OrderTableService orderTableService;
    private OrderService orderService;
    private OrderTableRepository orderTableRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        fakeKitchenridersClient = new FakeKitchenridersClient();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, fakeKitchenridersClient);
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName(value = "주문 테이블 등록 기능.")
    @Nested
    class OrderTablecreateTest {

        public static final String ORDER_TABLE_빈이름 = "";

        @DisplayName(value = "이름이 비어있으면 안됩니다.")
        @Test
        void valideOrderTableName() {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> orderTableService.create(createOrderTable(ORDER_TABLE_빈이름)));
        }

        @DisplayName(value = "주문 테이블 등록시 첫인원은 0명, 테이블은 사용가능으로 등록합니다.")
        @Test
        void createOrderTableName() {
            var orderTable = orderTableService.create(createOrderTable());

            assertAll(
                    () -> assertThat(orderTable.getNumberOfGuests()).isZero(),
                    () -> assertThat(orderTable.isOccupied()).isFalse()
            );
        }
    }

    @DisplayName(value = "주문 테이블 손님 배정 기능.")
    @Nested
    class OrderTableSitTest {
        @DisplayName(value = "주문 테이블 등록시 첫인원은 0명, 테이블은 사용가능으로 등록합니다.")
        @Test
        void sitOrderTable() {
            var orderTable = createOrderTable(ORDER_TABLE_DEFAULT_NAME);
            orderTableRepository.save(orderTable);
            var orderTableResponse = orderTableService.sit(orderTable.getId());
            assertThat(orderTableResponse.isOccupied()).isTrue();
        }
    }

    @DisplayName(value = "주문 테이블 손님 정리 기능.")
    @Nested
    class OrderTableClearTest {
        @DisplayName(value = "테이블의 주문상태가 주문 완료여야 합니다.")
        @Test
        void invalidOrderStatus() {
            var orderTable = createOrderTable(ORDER_TABLE_DEFAULT_NAME);
            var order = createOrder(ORDER_STATUS_제공완료, orderTable);
            orderRepository.save(order);
            orderTableRepository.save(orderTable);
            assertThatIllegalStateException().isThrownBy(() -> orderTableService.clear(orderTable.getId()));
        }

        @DisplayName(value = "테이블의 주문상태가 주문 완료여야 합니다.")
        @Test
        void clearOrderTable() {
            var orderTable = createOrderTable(ORDER_TABLE_DEFAULT_NAME);
            var order = createOrder(ORDER_STATUS_주문완료, orderTable);
            orderRepository.save(order);
            orderTableRepository.save(orderTable);
            OrderTable orderTableResponse = orderTableService.clear(orderTable.getId());

            assertAll(
                    () -> assertThat(orderTableResponse.getNumberOfGuests()).isZero(),
                    () -> assertThat(orderTableResponse.isOccupied()).isFalse()
            );
        }
    }

    @DisplayName(value = "테이블 인원 변경 기능.")
    @Nested
    class changeNumberOfGuestsTest {

        public static final int MINUS_NUMBER_OF_GUESTS = -1;
        public static final boolean 주문테이블_사용가능 = false;
        public static final int CHANGED_NUMBER_OF_GUESTS = 3;

        @DisplayName(value = "인원의 수는 음수가 되면 안됩니다.")
        @Test
        void invalidNumberOfGuests() {
            var orderTable = createOrderTable(ORDER_TABLE_DEFAULT_NAME, MINUS_NUMBER_OF_GUESTS);
            orderTableRepository.save(orderTable);
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
        }

        @DisplayName(value = "테이블이 사용가능 상태이면 안됩니다.")
        @Test
        void invalidOccupied() {
            var orderTable = createOrderTable(ORDER_TABLE_DEFAULT_NAME, 주문테이블_사용가능);
            orderTableRepository.save(orderTable);
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
        }

        @DisplayName(value = "테이블의 주문상태가 주문 완료여야 합니다.")
        @Test
        void changeNumberOfGuests() {
            var orderTable = createOrderTable(ORDER_TABLE_DEFAULT_NAME);
            orderTableRepository.save(orderTable);
            orderTable.setNumberOfGuests(CHANGED_NUMBER_OF_GUESTS);
            OrderTable orderTableResponse = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);
            assertThat(orderTableResponse.getNumberOfGuests()).isEqualTo(CHANGED_NUMBER_OF_GUESTS);
        }
    }

    @DisplayName(value = "모든 주물 테이블 조회 기능.")
    @Nested
    class OrderFindAllTest {

        @DisplayName(value = "모든 주문 테이블을 조회할 수 있다.")
        @Test
        void findAllTest() {
            var orderTable = createOrderTable(ORDER_TABLE_DEFAULT_NAME);
            orderTableRepository.save(orderTable);
            var orders = orderTableService.findAll();
            assertThat(orders.size()).isOne();
        }
    }
}
