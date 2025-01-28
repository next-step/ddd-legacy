package kitchenpos.application;

import kitchenpos.domain.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private OrderTableRepository orderTableRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setup() {
        Product product = createProduct(후라이드치킨_PRODUCT_UUID, 후라이드치킨_PRODUCT_NAME, 후라이드치킨_DEFAULT_PRICE);
        productRepository.save(product);

        MenuGroup menuGroup = createMenuGroup(치킨류_MENU_GROUP_UUID, 치킨류_MENU_GROUP_NAME);
        menuGroupRepository.save(menuGroup);

        List<MenuProduct> menuProducts = List.of(createMenuProduct(후라이드치킨_PRODUCT_UUID, product, 1));
        Menu menu = createMenu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_MENU_DEFAULT_PRICE, 치킨류_MENU_GROUP_UUID, menuGroup, menuProducts);
        menuRepository.save(menu);

        Menu noDisplayMenu = createMenu(후라이드치킨_NO_DISPLAY_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_MENU_DEFAULT_PRICE, 치킨류_MENU_GROUP_UUID, menuGroup, menuProducts);
        noDisplayMenu.setDisplayed(false);
        menuRepository.save(noDisplayMenu);
    }

    @DisplayName("주문 생성하기")
    @Nested
    class CreateOrderTest {

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문을 정상적으로 생성한다")
        @Test
        void create_order_successfully() {
            // given
            List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(후라이드치킨_MENU_UUID, 2, 후라이드치킨_MENU_DEFAULT_PRICE));
            Order request = createOrder(OrderType.DELIVERY, orderLineItems, "서울시 강남구");

            // when
            Order order = orderService.create(request);

            // then
            assertAll(
                    () -> assertThat(order.getId()).isNotNull(),
                    () -> assertThat(order.getType()).isEqualTo(request.getType()),
                    () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(order.getOrderLineItems()).hasSize(orderLineItems.size()),
                    () -> assertThat(order.getDeliveryAddress()).isEqualTo(request.getDeliveryAddress())
            );
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문 항목에 포함된 메뉴는 전시중인 상태여야 한다")
        @Test
        void menu_should_be_displayed() {
            // given
            List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(후라이드치킨_NO_DISPLAY_MENU_UUID, 2, 후라이드치킨_MENU_DEFAULT_PRICE));
            Order request = createOrder(OrderType.DELIVERY, orderLineItems, "서울시 강남구");

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(request);

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);

        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문 유형은 필수로 지정해야 한다")
        @Test
        void order_type_must_be_specified() {
            // given
            List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(후라이드치킨_MENU_UUID, 2, 후라이드치킨_MENU_DEFAULT_PRICE));
            Order request = createOrder(null, orderLineItems, "서울시 강남구");

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문 항목은 1개 이상 포함되어야 한다")
        @Test
        void order_must_contain_at_least_one_item() {
            // given
            Order request = createOrder(OrderType.DELIVERY, Collections.emptyList(), "서울시 강남구");

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("배달 주문의 경우 배송 주소를 필수로 입력해야 한다")
        @Test
        void delivery_address_must_be_specified_for_delivery_order() {
            // given
            List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(후라이드치킨_MENU_UUID, 2, new BigDecimal(19000)));
            Order request = createOrder(OrderType.DELIVERY, orderLineItems, null);

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("매장 식사 주문의 경우, 주문 테이블을 반드시 지정해야 한다")
        @Test
        void order_table_must_be_specified_for_eat_in_order() {
            // given
            List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(후라이드치킨_MENU_UUID, 2, 후라이드치킨_MENU_DEFAULT_PRICE));
            Order request = createOrder(OrderType.EAT_IN, orderLineItems, null, 테이블_1_ORDER_TABLE_UUID, null);

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(request);

            // then
            assertThatThrownBy(throwingCallable)
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("매장 식사 주문의 경우, 테이블이 사용 중 상태여야 한다")
        @Test
        void table_must_be_occupied_for_eat_in_order() {
            // given
            OrderTable orderTable = createOrderTable(테이블_1_ORDER_TABLE_UUID, "테이블 1", 0, false);
            orderTableRepository.save(orderTable);

            List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(후라이드치킨_MENU_UUID, 2, 후라이드치킨_MENU_DEFAULT_PRICE));
            Order request = createOrder(OrderType.EAT_IN, orderLineItems, null, 테이블_1_ORDER_TABLE_UUID, orderTable);

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(request);

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("주문 수락하기")
    @Nested
    class AcceptOrderTest {
        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문을 정상적으로 수락한다")
        @Test
        void accept_order_successfully() {
            // given
            Order order = createDeleveryOrder();

            // when
            Order acceptedOrder = orderService.accept(order.getId());

            // then
            assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문 상태가 '대기' 상태인 경우에만 수락할 수 있다")
        @Test
        void only_waiting_order_can_be_accepted() {
            // given
            Order order = createDeleveryOrder();
            orderService.accept(order.getId());

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.accept(order.getId());

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("주문 제공하기")
    @Nested
    class ServeOrderTest {

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문을 정상적으로 제공한다")
        @Test
        void serve_order_successfully() {
            // given
            Order order = createDeleveryOrder();
            orderService.accept(order.getId());

            // when
            Order servedOrder = orderService.serve(order.getId());

            // then
            assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문 상태가 '수락됨' 상태인 경우에만 제공할 수 있다")
        @Test
        void only_accepted_order_can_be_served() {
            // given
            Order order = createDeleveryOrder();

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.serve(order.getId());

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("배달 시작하기")
    @Nested
    class StartDeliveryOrderTest {

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("배달 주문을 정상적으로 시작한다")
        @Test
        void start_delivery_order_successfully() {
            // given
            Order order = createDeleveryOrder();
            orderService.accept(order.getId());
            orderService.serve(order.getId());

            // when
            Order startedOrder = orderService.startDelivery(order.getId());

            // then
            assertThat(startedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문 유형이 '배달'인 경우에만 배달을 시작할 수 있다")
        @Test
        void only_delivery_order_can_be_started_for_delivery() {
            // given
            Order order = createEeaInOrder();
            orderService.accept(order.getId());
            orderService.serve(order.getId());

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.startDelivery(order.getId());

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문 상태가 '제공됨' 상태인 경우에만 배달을 시작할 수 있다")
        @Test
        void only_served_order_can_be_started_for_delivery() {
            // given
            Order order = createDeleveryOrder();
            orderService.accept(order.getId());

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.startDelivery(order.getId());

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("배달 완료하기")
    @Nested
    class CompleteDeliveryOrderTest {

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("배달 주문을 정상적으로 완료한다")
        @Test
        void complete_delivery_order_successfully() {
            // given
            Order order = createDeleveryOrder();
            orderService.accept(order.getId());
            orderService.serve(order.getId());
            orderService.startDelivery(order.getId());

            // when
            Order completedOrder = orderService.completeDelivery(order.getId());

            // then
            assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문 유형이 '배달'인 경우에만 배달을 완료할 수 있다")
        @Test
        void only_delivery_order_can_be_completed_for_delivery() {
            // given
            Order order = createEeaInOrder();
            orderService.accept(order.getId());
            orderService.serve(order.getId());

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.completeDelivery(order.getId());

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문 상태가 '배달 중' 상태인 경우에만 배달을 완료할 수 있다")
        @Test
        void only_delivering_order_can_be_completed_for_delivery() {
            // given
            Order order = createDeleveryOrder();
            orderService.accept(order.getId());
            orderService.serve(order.getId());

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.completeDelivery(order.getId());

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("주문 완료하기")
    @Nested
    class CompleteOrderTest {

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("배달 주문을 정상적으로 완료한다")
        @Test
        void complete_delivery_order_successfully() {
            // given
            Order order = createDeleveryOrder();
            orderService.accept(order.getId());
            orderService.serve(order.getId());
            orderService.startDelivery(order.getId());
            orderService.completeDelivery(order.getId());

            // when
            Order completedOrder = orderService.complete(order.getId());

            // then
            assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("매장 식사 주문을 정상적으로 완료한다")
        @Test
        void complete_eat_in_order_successfully() {
            // given
            Order order = createEeaInOrder();
            orderService.accept(order.getId());
            orderService.serve(order.getId());

            // when
            Order completedOrder = orderService.complete(order.getId());

            // then
            assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문 상태가 '배달 완료' 상태인 경우에만 배달 주문을 완료할 수 있다")
        @Test
        void only_delivered_order_can_be_completed_for_delivery() {
            // given
            Order order = createDeleveryOrder();

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.complete(order.getId());

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문 상태가 '제공됨' 상태인 경우에만 매장 식사 주문을 완료할 수 있다")
        @Test
        void only_served_order_can_be_completed_for_eat_in() {
            // given
            Order order = createEeaInOrder();

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.complete(order.getId());

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("주문 목록 조회하기")
    @Nested
    class FindAllOrdersTest {
        private static final int TOTAL_ORDER_COUNT = 3;

        @SqlGroup({
                @Sql(value = "/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
                @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        })
        @DisplayName("모든 주문을 조회한다")
        @Test
        void find_all_orders() {
            // when
            List<Order> orders = orderService.findAll();

            // then
            assertThat(orders).hasSize(TOTAL_ORDER_COUNT);
        }
    }

    private Order createDeleveryOrder() {
        List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(후라이드치킨_MENU_UUID, 2, 후라이드치킨_MENU_DEFAULT_PRICE));
        Order request = createOrder(OrderType.DELIVERY, orderLineItems, "서울시 강남구");
        return orderService.create(request);
    }

    private Order createEeaInOrder() {
        OrderTable orderTable = createOrderTable(테이블_1_ORDER_TABLE_UUID, "테이블 1", 0, true);
        orderTableRepository.save(orderTable);

        List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(후라이드치킨_MENU_UUID, 2, 후라이드치킨_MENU_DEFAULT_PRICE));
        Order request = createOrder(OrderType.EAT_IN, orderLineItems, "서울시 강남구", 테이블_1_ORDER_TABLE_UUID, orderTable);
        return orderService.create(request);
    }

    private static MenuProduct createMenuProduct(UUID productId, Product proudct, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setProduct(proudct);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private static Product createProduct(UUID id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private static MenuGroup createMenuGroup(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    private static Order createOrder(OrderType type, List<OrderLineItem> orderLineItems, String deliveryAddress) {
        return createOrder(type, orderLineItems, deliveryAddress, null, null);
    }

    private static Order createOrder(OrderType type, List<OrderLineItem> orderLineItems, String deliveryAddress, UUID orderTableUuid, OrderTable orderTable) {
        Order order = new Order();
        order.setType(type);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTableId(orderTableUuid);
        order.setOrderTable(orderTable);
        return order;
    }

    private static OrderLineItem createOrderLineItem(UUID menuId, int quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    private static Menu createMenu(UUID id, String name, BigDecimal price, UUID menuGroupId, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        return menu;
    }

    private static OrderTable createOrderTable(UUID id, String name, int numberOfGuests, boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setName(name);
        return orderTable;
    }

    private static final UUID 후라이드치킨_PRODUCT_UUID = UUID.randomUUID();
    private static final UUID 후라이드치킨_MENU_UUID = UUID.randomUUID();
    private static final UUID 후라이드치킨_NO_DISPLAY_MENU_UUID = UUID.randomUUID();
    private static final UUID 치킨류_MENU_GROUP_UUID = UUID.randomUUID();
    private static final UUID 테이블_1_ORDER_TABLE_UUID = UUID.randomUUID();
    private static final String 후라이드치킨_PRODUCT_NAME = "후라이드치킨";
    private static final String 치킨류_MENU_GROUP_NAME = "치킨류";
    private static final String 후라이드치킨_MENU_NAME = "후라이드치킨";
    private static final BigDecimal 후라이드치킨_DEFAULT_PRICE = new BigDecimal(20000);
    private static final BigDecimal 후라이드치킨_MENU_DEFAULT_PRICE = new BigDecimal(19000);
}