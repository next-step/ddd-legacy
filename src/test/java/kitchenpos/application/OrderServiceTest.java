package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@DisplayName("OrderService 클래스의")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private OrderTableRepository orderTableRepository;
    @MockBean
    private KitchenridersClient kitchenridersClient;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;

    private OrderTable orderTable;
    private Menu menu;
    private Order orderRequest;
    private OrderLineItem orderLineItemRequest;

    @BeforeEach
    void setUp() {

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("menuGroup");
        menuGroupRepository.save(menuGroup);

        orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("orderTable");
        orderTable.setNumberOfGuests(2);
        orderTable.setOccupied(true);
        orderTableRepository.save(orderTable);

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("product");
        product.setPrice(BigDecimal.valueOf(1000));
        productRepository.save(product);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1);

        menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("menu");
        menu.setPrice(BigDecimal.valueOf(1000));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setDisplayed(true);
        menuRepository.save(menu);

        orderLineItemRequest = new OrderLineItem();
        orderLineItemRequest.setMenuId(menu.getId());
        orderLineItemRequest.setQuantity(1);
        orderLineItemRequest.setPrice(menu.getPrice());

        orderRequest = new Order();
        orderRequest.setOrderTableId(orderTable.getId());
        orderRequest.setType(OrderType.EAT_IN);
        orderRequest.setOrderLineItems(List.of(orderLineItemRequest));
    }

    @DisplayName("create 메소드는")
    @Nested
    class Create {

        @DisplayName("주문 방법이 존재하지 않을 경우 예외를 던진다.")
        @Test
        void createWithNullOrderType() {
            // given
            orderRequest.setType(null);
            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("주문 항목이 존재하지 않을 경우 예외를 던진다.")
        @ParameterizedTest
        @MethodSource("provideInvalidOrderLineItems")
        void createWithInvalidOrderLineItems(List<OrderLineItem> invalidOrderLineItem) {
            // given
            orderRequest.setOrderLineItems(invalidOrderLineItem);
            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("매장 이외의 주문에 대해 주문 항목의 매뉴의 갯수가 0개 미만일 경우 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {"DELIVERY", "TAKEOUT"})
        void createWithNegativeQuantity(String orderType) {
            // given
            orderRequest.setType(OrderType.valueOf(orderType));
            orderLineItemRequest.setQuantity(-1);
            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(orderRequest));
        }


        @DisplayName("주문 항목의 메뉴가 존재하지 않을 경우 예외를 던진다.")
        @Test
        void createWithNonExistentMenu() {
            // given
            orderLineItemRequest.setMenuId(UUID.randomUUID());
            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("주문 항목의 메뉴가 미노출 상태일 경우 예외를 던진다.")
        @Test
        void createWithNonDisplayedMenu() {
            // given
            menu.setDisplayed(false);
            menuRepository.save(menu);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("주문 항목의 가격과 메뉴의 가격아 다를 경우 예외를 던진다.")
        @Test
        void createWithDifferentMenuPrice() {
            // given
            orderLineItemRequest.setPrice(BigDecimal.valueOf(100));
            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("배달 주문에 대해 배달 주소가 없을 경우 예외를 던진다.")
        @Test
        void createWithNullDeliveryAddress() {
            // given
            orderRequest.setType(OrderType.DELIVERY);
            orderRequest.setDeliveryAddress(null);
            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("매장 주문에 대해 주문 테이블이 존재하지 않을 경우 예외를 던진다.")
        @Test
        void createWithNonExistentOrderTable() {
            // given
            orderRequest.setType(OrderType.EAT_IN);
            orderRequest.setOrderTableId(UUID.randomUUID());
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("매장 주문에 대해 해당 테이블의 착석 상태가 아닐 경우 예외를 던진다.")
        @Test
        void createWithNonOccupiedOrderTable() {
            // given
            orderRequest.setType(OrderType.EAT_IN);
            orderTable.setOccupied(false);
            orderTableRepository.save(orderTable);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("매장 주문이 등록된다.")
        @Test
        void create() {
            // when
            Order order = orderService.create(orderRequest);
            // then
            assertNotNull(order.getId());
            assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(order.getType()).isEqualTo(OrderType.EAT_IN);
        }

        @DisplayName("배달 주문이 등록된다.")
        @Test
        void createWithDelivery() {
            // given
            orderRequest.setType(OrderType.DELIVERY);
            orderRequest.setDeliveryAddress("deliveryAddress");
            // when
            Order order = orderService.create(orderRequest);
            // then
            assertNotNull(order.getId());
            assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(order.getType()).isEqualTo(OrderType.DELIVERY);
            assertThat(order.getDeliveryAddress()).isEqualTo("deliveryAddress");
        }

        @DisplayName("포장 주문이 등록된다.")
        @Test
        void createWithTakeout() {
            // given
            orderRequest.setType(OrderType.TAKEOUT);
            // when
            Order order = orderService.create(orderRequest);
            // then
            assertNotNull(order.getId());
            assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(order.getType()).isEqualTo(OrderType.TAKEOUT);
        }

        private static Stream<List<OrderLineItem>> provideInvalidOrderLineItems() {
            return Stream.of(null, List.of());
        }
    }

    @DisplayName("accept 메소드는")
    @Nested
    class Accept {

        private Order order;

        @BeforeEach
        void setUp() {
            order = orderService.create(orderRequest);
        }

        @DisplayName("주문이 존재하지 않을 경우 예외를 던진다.")
        @Test
        void acceptWithNonExistentOrder() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderService.accept(UUID.randomUUID()));
        }

        @DisplayName("주문 상태가 대기(WAITING) 상태가 아닐 경우 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {"ACCEPTED", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void acceptWithNonWaitingOrder(String orderStatus) {
            // given
            order.setStatus(OrderStatus.valueOf(orderStatus));
            orderRepository.save(order);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.accept(order.getId()));
        }

        @DisplayName("배달 주문에 대해 주문 항목의 총 가격을 계산하여 라이더에게 배달 요청한다.")
        @Test
        void acceptWithDelivery() {
            // given
            order.setType(OrderType.DELIVERY);
            order.setOrderTableId(null);
            order.setDeliveryAddress("deliveryAddress");
            orderRepository.save(order);
            doNothing().when(kitchenridersClient).requestDelivery(any(), any(), any());

            // when
            Order accepted = orderService.accept(order.getId());
            // then
            verify(kitchenridersClient).requestDelivery(accepted.getId(),
                                                        accepted.getOrderLineItems().get(0).getMenu().getPrice(),
                                                        accepted.getDeliveryAddress());
            assertThat(accepted.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("매장 주문에 대해 주문 상태를 접수로 변경한다.")
        @Test
        void accept() {
            // when
            Order accepted = orderService.accept(order.getId());
            // then
            assertThat(accepted.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }
    }

    @DisplayName("serve 메소드는")
    @Nested
    class Serve {

        private Order order;

        @BeforeEach
        void setUp() {
            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
        }

        @DisplayName("주문이 존재하지 않을 경우 예외를 던진다.")
        @Test
        void serveWithNonExistentOrder() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderService.serve(UUID.randomUUID()));
        }

        @DisplayName("주문 상태가 접수(ACCEPTED) 상태가 아닐 경우 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {"WAITING", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void serveWithNonAcceptedOrder(String orderStatus) {
            // given
            order.setStatus(OrderStatus.valueOf(orderStatus));
            orderRepository.save(order);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.serve(order.getId()));
        }

        @DisplayName("주문 상태를 제공 상태로 변경한다.")
        @Test
        void serve() {
            // when
            Order served = orderService.serve(order.getId());
            // then
            assertThat(served.getStatus()).isEqualTo(OrderStatus.SERVED);
        }
    }

    @DisplayName("startDelivery 메소드는")
    @Nested
    class StartDelivery {

        private Order order;

        @BeforeEach
        void setUp() {
            orderRequest.setType(OrderType.DELIVERY);
            orderRequest.setDeliveryAddress("deliveryAddress");
            orderRequest.setOrderTableId(null);

            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());
        }

        @DisplayName("주문이 존재하지 않을 경우 예외를 던진다.")
        @Test
        void startDeliveryWithNonExistentOrder() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderService.startDelivery(UUID.randomUUID()));
        }

        @DisplayName("주문 방법이 배달이 아닐 경우 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {"EAT_IN", "TAKEOUT"})
        void startDeliveryWithNonDeliveryOrder(String orderType) {
            // given
            order.setType(OrderType.valueOf(orderType));
            orderRepository.save(order);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.startDelivery(order.getId()));
        }

        @DisplayName("주문 상태가 제공(SERVED) 상태가 아닐 경우 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void startDeliveryWithNonServedOrder(String orderStatus) {
            // given
            order.setStatus(OrderStatus.valueOf(orderStatus));
            orderRepository.save(order);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.startDelivery(order.getId()));
        }

        @DisplayName("주문 상태를 배달 중 상태로 변경한다.")
        @Test
        void startDelivery() {
            // when
            Order delivering = orderService.startDelivery(order.getId());
            // then
            assertThat(delivering.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }
    }

    @DisplayName("completeDelivery 메소드는")
    @Nested
    class CompleteDelivery {

        private Order order;

        @BeforeEach
        void setUp() {
            orderRequest.setType(OrderType.DELIVERY);
            orderRequest.setDeliveryAddress("deliveryAddress");
            orderRequest.setOrderTableId(null);

            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());
            order = orderService.startDelivery(order.getId());
        }

        @DisplayName("주문이 존재하지 않을 경우 예외를 던진다.")
        @Test
        void completeDeliveryWithNonExistentOrder() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderService.completeDelivery(UUID.randomUUID()));
        }

        @DisplayName("주문 상태가 배달 중(DELIVERING) 상태가 아닐 경우 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
        void completeDeliveryWithNonDeliveringOrder(String orderStatus) {
            // given
            order.setStatus(OrderStatus.valueOf(orderStatus));
            orderRepository.save(order);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.completeDelivery(order.getId()));
        }

        @DisplayName("주문 상태를 배달 완료 상태로 변경한다.")
        @Test
        void completeDelivery() {
            // when
            Order delivered = orderService.completeDelivery(order.getId());
            // then
            assertThat(delivered.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
    }

    @DisplayName("complete 메소드는")
    @Nested
    class Complete {

        private Order order;

        @DisplayName("주문이 존재하지 않을 경우 예외를 던진다.")
        @Test
        void completeWithNonExistentOrder() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderService.complete(UUID.randomUUID()));
        }

        @DisplayName("매장 주문이거나, 포장주문 일 경우 주문 상태가 제공(SERVED) 상태가 아닐 경우 예외를 던진다.")
        @ParameterizedTest
        @ValueSource(strings = {"EAT_IN", "TAKEOUT"})
        void completeWithNonServedOrder(String orderType) {
            // given
            orderRequest.setType(OrderType.valueOf(orderType));
            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.complete(order.getId()));
        }

        @DisplayName("배달 주문일 경우 주문 상태가 배달 완료 상태가 아닐 경우 예외를 던진다.")
        @Test
        void completeWithNonDeliveredOrder() {
            // given
            orderRequest.setType(OrderType.DELIVERY);
            orderRequest.setDeliveryAddress("deliveryAddress");
            orderRequest.setOrderTableId(null);

            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());
            order = orderService.startDelivery(order.getId());
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.complete(order.getId()));
        }

        @DisplayName("매장 주문일 경우 완료 처리 후 주문 테이블의 착석 상태를 미착석으로 변경한다.")
        @Test
        void completeWithEatIn() {
            // given
            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());
            // when
            Order completed = orderService.complete(order.getId());
            // then
            assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(completed.getType()).isEqualTo(OrderType.EAT_IN);
            assertThat(orderTableRepository.findById(order.getOrderTable().getId()).get().isOccupied()).isFalse();
            assertThat(orderTableRepository.findById(order.getOrderTable().getId()).get().getNumberOfGuests()).isEqualTo(0);
        }

        @DisplayName("포장 주문에 대해 완료 처리 한다.")
        @Test
        void completeWithTakeout() {
            // given
            orderRequest.setType(OrderType.TAKEOUT);
            orderRequest.setOrderTableId(null);
            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());
            // when
            Order completed = orderService.complete(order.getId());
            // then
            assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(completed.getType()).isEqualTo(OrderType.TAKEOUT);
        }

        @DisplayName("배달 주문에 대해 완료 처리 한다.")
        @Test
        void completeWithDelivery() {
            // given
            orderRequest.setType(OrderType.DELIVERY);
            orderRequest.setDeliveryAddress("deliveryAddress");
            orderRequest.setOrderTableId(null);

            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());
            order = orderService.startDelivery(order.getId());
            order = orderService.completeDelivery(order.getId());
            // when
            Order completed = orderService.complete(order.getId());
            // then
            assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(completed.getType()).isEqualTo(OrderType.DELIVERY);
        }
    }

    @DisplayName("findAll 메소드는")
    @Nested
    class FindAll {

        @DisplayName("주문이 존재하지 않을 경우 빈 리스트를 반환한다.")
        @Test
        void findAllWithEmpty() {
            // when
            List<Order> orders = orderService.findAll();
            // then
            assertThat(orders).isEmpty();
        }

        @DisplayName("주문이 존재할 경우 주문 리스트를 반환한다.")
        @Test
        void findAll() {
            // given
            orderService.create(orderRequest);
            // when
            List<Order> orders = orderService.findAll();
            // then
            assertThat(orders).isNotEmpty();
        }
    }
}
