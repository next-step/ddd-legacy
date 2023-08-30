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

        Menu menu = createMenu(savedMenuGroup,
                               List.of(createMenuProduct(1L, savedProducts.get(0), 1L),
                                       createMenuProduct(2L, savedProducts.get(1), 1L)),
                               new BigDecimal("20000"));
        menu.setDisplayed(displayed);

        return menuRepository.save(menu);
    }

    public Menu getMenu() {
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("상품1", new BigDecimal("10000"));
        Product product2 = createProduct("상품2", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        return createMenu(savedMenuGroup,
                          List.of(createMenuProduct(1L, savedProducts.get(0), 1L),
                                  createMenuProduct(2L, savedProducts.get(1), 1L)),
                          new BigDecimal("20000"));
    }

}
