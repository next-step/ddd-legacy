package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static kitchenpos.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private KitchenridersClient kitchenridersClient;

    @Test
    @DisplayName("포장 주문 한다.")
    void create_takeout01() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, List.of(orderLineItem), null);

        Order createdOrder = orderService.create(order);

        assertThat(createdOrder).isNotNull();
    }

    @Test
    @DisplayName("배달 주문 한다.")
    void create_delivery01() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.WAITING, OrderType.DELIVERY, List.of(orderLineItem), "주소");

        Order createdOrder = orderService.create(order);

        assertThat(createdOrder).isNotNull();
    }

    @Test
    @DisplayName("배달 주문을 할 땐 배송지를 입력해야 한다.")
    void create_delivery02() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.WAITING, OrderType.DELIVERY, List.of(orderLineItem), null);

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("매장 주문을 한다.")
    void create_eat_in01() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.WAITING, OrderType.EAT_IN, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());

        Order createdOrder = orderService.create(order);

        assertThat(createdOrder).isNotNull();
    }

    @Test
    @DisplayName("매장주문 시 테이블이 점유되어있지 않으면 주문할 수 없다.")
    void create_eat_in02() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.WAITING, OrderType.EAT_IN, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(false));
        order.setOrderTableId(getSavedOrderTable(false).getId());

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("매장주문 시 테이블이 점유되어있지 않으면 주문할 수 없다.")
    void create_eat_in03() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.WAITING, OrderType.EAT_IN, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(false));
        order.setOrderTableId(getSavedOrderTable(false).getId());

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 라인 아이템은 비어있을 수 없다.")
    void create01() {
        Order order = createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, Collections.emptyList(), null);

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 타입은 비어있을 수 없다.")
    void create02() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.WAITING, null, List.of(orderLineItem), null);

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 된 메뉴는 이미 등록 되어 있어야 한다.")
    void create03() {
        Menu menu = getMenu();
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, List.of(orderLineItem), null);

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("매장 주문이 아니면 수량은 0보다 작을 수 없다.")
    void create04() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        orderLineItem.setQuantity(-1);
        Order order = createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, List.of(orderLineItem), null);

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 된 메뉴가 전시되지 않으면 주문할 수 없다.")
    void create05() {
        Menu menu = getSavedMenu(false);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, List.of(orderLineItem), null);

        assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문이 수락되는 경우, 주문상태를 접수로 변경한다.")
    void accept01() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        Order acceptedOrder = orderService.accept(savedOrder.getId());

        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("주문 수락의 경우 주문상태가 대기만 가능하다.")
    void accept02() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.ACCEPTED, OrderType.TAKEOUT, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        assertThatThrownBy(() -> orderService.accept(savedOrder.getId())).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문이 제공되면 주문상태를 주문제공으로 변경한다.")
    void serve01() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.ACCEPTED, OrderType.DELIVERY, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        Order acceptedOrder = orderService.serve(savedOrder.getId());

        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    @DisplayName("주문이 제공되는 경우 주문상태가 접수만 가능하다.")
    void serve02() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.WAITING, OrderType.DELIVERY, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        assertThatThrownBy(() -> orderService.serve(savedOrder.getId())).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("배달이 시작되는 경우 주문상태를 배달중으로 변경한다.")
    void startDelivery01() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.SERVED, OrderType.DELIVERY, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        Order acceptedOrder = orderService.startDelivery(savedOrder.getId());

        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("배달이 시작되는 경우 주문상태가 주문제공만 가능하다.")
    void startDelivery02() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.ACCEPTED, OrderType.DELIVERY, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        assertThatThrownBy(() -> orderService.startDelivery(savedOrder.getId())).isInstanceOf(
                IllegalStateException.class);
    }

    @Test
    @DisplayName("배달이 시작되는 경우 주문상태가 주문제공만 가능하다.")
    void startDelivery03() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.SERVED, OrderType.EAT_IN, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        assertThatThrownBy(() -> orderService.startDelivery(savedOrder.getId())).isInstanceOf(
                IllegalStateException.class);
    }

    @Test
    @DisplayName("배달이 종료되는 경우 주문상태를 배달완료로 변경한다.")
    void completeDelivery01() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.DELIVERING, OrderType.DELIVERY, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        Order completeDelivery = orderService.completeDelivery(savedOrder.getId());

        assertThat(completeDelivery.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("배달이 종료되는 경우 주문상태가 배달주문만 가능하다.")
    void completeDelivery02() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.ACCEPTED, OrderType.DELIVERY, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        assertThatThrownBy(() -> orderService.completeDelivery(savedOrder.getId())).isInstanceOf(
                IllegalStateException.class);
    }

    @Test
    @DisplayName("주문을 종료하는 경우 주문상태를 완료 로 변경한다.")
    void complete01() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.SERVED, OrderType.TAKEOUT, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        Order complete = orderService.complete(savedOrder.getId());

        assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("주문 종료에서 주문타입이 배달주문인경우, 주문상태가 배달완료면 안된다.")
    void complete02() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.SERVED, OrderType.DELIVERY, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        assertThatThrownBy(() -> orderService.complete(savedOrder.getId())).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 종료에서 주문타입이 포장주문 또는, 매장주문인 경우, 주문상태는 주문제공이면 안된다.")
    void complete03() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        assertThatThrownBy(() -> orderService.complete(savedOrder.getId())).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문타입이 매장주문 경우, 해당 테이블의 종료된 상태면 손님의 수와, 테이블 여부를 초기화 한다.")
    void complete04() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.SERVED, OrderType.EAT_IN, List.of(orderLineItem), null);
        order.setOrderTable(getSavedOrderTable(true));
        order.setOrderTableId(getSavedOrderTable(true).getId());
        Order savedOrder = orderRepository.save(order);

        Order complete = orderService.complete(savedOrder.getId());

        assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        OrderTable orderTable = complete.getOrderTable();
        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isOccupied()).isFalse();
    }

    private OrderTable getSavedOrderTable(boolean occupied) {
        OrderTable orderTable = createOrderTable("테이블", occupied);
        return orderTableRepository.save(orderTable);
    }

    public Menu getSavedMenu(boolean displayed) {
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        Menu menu = createMenu("메뉴이름", new BigDecimal("20000"), savedMenuGroup,
                               List.of(createMenuProduct(1L, savedProducts.get(0), 1L),
                                       createMenuProduct(2L, savedProducts.get(1), 1L))
        );
        menu.setDisplayed(displayed);

        return menuRepository.save(menu);
    }

    public Menu getMenu() {
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        return createMenu("메뉴이름", new BigDecimal("20000"), savedMenuGroup,
                          List.of(createMenuProduct(1L, savedProducts.get(0), 1L),
                                  createMenuProduct(2L, savedProducts.get(1), 1L))
        );
    }

}
