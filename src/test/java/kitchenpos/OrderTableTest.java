package kitchenpos;

import kitchenpos.application.*;
import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.DELIVERY;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.spy;


@DisplayName(value = " OrderTable 테스트")
public class OrderTableTest {

   private static final UUID ORDER_UUID = UUID.fromString("69d78f38-3bff-457c-bb72-26319c985fd8");
    private static final BigDecimal 후라이드치킨_DEFAULT_PRICE = new BigDecimal(20000);
     private static final UUID 후라이드치킨_MENU_UUID = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b");
     private static final OrderStatus ORDER_STATUS_제공완료 = SERVED;
    private static final OrderStatus ORDER_STATUS_주문완료 = COMPLETED;
    private static final OrderType ORDER_TYPE_배달주문 = DELIVERY;

    private static final LocalDateTime ORDER_DATE_TIME_주문요청시간 = LocalDateTime.now();
    private static final int DEFAULT_QUANTITY = 1;
    public static final String ORDER_TABLE_DEFAULT_NAME = "1번";


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

    private OrderTable createOrderTable() {
        var orderTable = new OrderTable();
        orderTable.setName("1번");
        return orderTable;
    }

    private OrderTable createOrderTable(String name) {
        return createOrderTable(name,1);
    }

    private OrderTable createOrderTable(String name, int numberOfGuests) {
        var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(true);
        return orderTable;
    }

    private OrderTable createOrderTable(String name, boolean isOcupied) {
        var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(1);
        orderTable.setOccupied(isOcupied);
        return orderTable;
    }

    private static Order createOrder(OrderStatus orderStatus, OrderTable orderTable) {
        return createOrder(ORDER_UUID, ORDER_TYPE_배달주문, orderStatus, ORDER_DATE_TIME_주문요청시간,
                createOrderLineItem(), "강남구", orderTable);
    }

    private static Order createOrder(UUID id, OrderType orderType, OrderStatus orderStatus, LocalDateTime orderDateTime, OrderLineItem orderLineItem, String deliveryAddress, OrderTable orderTable) {
        Order order = new Order();
        order.setId(id);
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderDateTime(orderDateTime);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        return order;
    }

    private static OrderLineItem createOrderLineItem(UUID menuId, long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    private static OrderLineItem createOrderLineItem() {
        return createOrderLineItem(후라이드치킨_MENU_UUID, DEFAULT_QUANTITY, 후라이드치킨_DEFAULT_PRICE);
    }
}
