package kitchenpos.application;

import kitchenpos.application.fake.FakeMenuRepository;
import kitchenpos.application.fake.FakeOrderRepository;
import kitchenpos.application.fake.FakeOrderTableRepository;
import kitchenpos.application.fake.FakeRidersClient;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
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
import kitchenpos.domain.RidersClient;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class OrderServiceTest {

    private final OrderRepository orderRepository = new FakeOrderRepository();
    private final MenuRepository menuRepository = new FakeMenuRepository();
    private final OrderTableRepository orderTableRepository = new FakeOrderTableRepository();
    private final RidersClient ridersClient = new FakeRidersClient();

    private final OrderService service = new OrderService(orderRepository, menuRepository, orderTableRepository, ridersClient);

    @Test
    @DisplayName("`주문`은 `메뉴`와 주문을 받을 방법을 배달로 선택 하여 주문할 수 있다.")
    void create_delivery() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        ArrayList<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        String address = "주소";
        Order order = Order.ofDelivery(orderLineItems, address);

        Order savedOrder = service.create(order);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(savedOrder.getType()).isEqualTo(OrderType.DELIVERY);
        assertThat(savedOrder.getDeliveryAddress()).isEqualTo(address);
    }

    @Test
    @DisplayName("`주문`은 `메뉴`와 주문을 받을 방법을 포장으로 선택 하여 주문할 수 있다.")
    void create_takeout() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        Order order = Order.ofTakeOut(orderLineItems);

        Order savedOrder = service.create(order);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(savedOrder.getType()).isEqualTo(OrderType.TAKEOUT);
    }

    @Test
    @DisplayName("`주문`은 `메뉴`와 주문을 받을 방법을 매장으로 선택 하여 주문할 수 있다.")
    void create_eat_it() {
        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        OrderTable orderTable = orderTableRepository.save(createOrderTable("테이블1"));
        Order order = Order.ofEatIt(orderLineItems, orderTable);

        Order savedOrder = service.create(order);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(savedOrder.getType()).isEqualTo(OrderType.EAT_IN);
    }

    @Test
    @DisplayName("매장 주문이 아닐 때 주문한 메뉴의 수량이 0이하면 주문할 수 없다.")
    void create_not_quantity_zero() {
        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu(("메뉴 이름2")));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, -1), new OrderLineItem(menu2, 1));

        Order order = Order.ofTakeOut(orderLineItems);
        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("`메뉴`가 노출 중이 아니라면 주문할 수 없다.")
    void create_not_hide_menu() {
        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1", false));
        Menu menu2 = menuRepository.save(createMenu(("메뉴 이름2")));
        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        OrderTable orderTable = orderTableRepository.save(createOrderTable("테이블1"));

        Order order = Order.ofEatIt(orderLineItems, orderTable);
        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("고객이 주문한 메뉴의 가격과 실제 메뉴의 가격이 다른 경우 주문할 수 없다.")
    void create_not_different_price() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2, BigDecimal.valueOf(100L)),
                new OrderLineItem(menu2, 1));

        Order order = Order.ofTakeOut(orderLineItems);

        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("배달 주문일 때 배송 주소가 비어있다면 주문할 수 없다.")
    @NullAndEmptySource()
    void create_not_empty_address(String address) {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        ArrayList<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        Order order = Order.ofDelivery(orderLineItems, address);

        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("매장 주문일 때 `주문 테이블`이 사용할 수 있는 상태가 아니라면 주문할 수 없다.")
    void create_order_table() {
        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        OrderTable orderTable = orderTableRepository.save(createOrderTable("테이블1", false));

        Order order = Order.ofEatIt(orderLineItems, orderTable);
        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("식당에서 `주문`을 수락할 수 있다.")
    void accept() {
        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        Order order = Order.ofTakeOut(orderLineItems);

        Order savedOrder = service.create(order);

        Order acceptdOrder = service.accept(savedOrder.getId());

        assertThat(acceptdOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("주문 대기인 `주문`만 수락할 수 있다.")
    void accept_status() {
        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        Order order = Order.ofTakeOut(orderLineItems);

        Order savedOrder = service.create(order);
        savedOrder.setStatus(OrderStatus.COMPLETED);

        assertThatThrownBy(() -> service.accept(savedOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("식당에서 `주문`을 제공할 수 있는 상태로 변경할 수 있다.")
    void serve() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        Order order = Order.ofTakeOut(orderLineItems);

        Order savedOrder = service.create(order);

        Order acceptOrder = service.accept(savedOrder.getId());
        Order serveOrder = service.serve(acceptOrder.getId());

        assertThat(serveOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    @DisplayName("주문 수락 상태가 아니라면 제공 상태로 변경할 수 없다.")
    void serve_status() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        Order order = Order.ofTakeOut(orderLineItems);

        Order savedOrder = service.create(order);

        assertThatThrownBy(() -> service.serve(savedOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("`주문`을 배달 시작할 수 있다.")
    void startDelivery() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        String address = "주소";
        Order order = Order.ofDelivery(orderLineItems, address);

        Order savedOrder = service.create(order);

        Order acceptOrder = service.accept(savedOrder.getId());
        Order serveOrder = service.serve(acceptOrder.getId());
        Order startDeliveryOrder = service.startDelivery(serveOrder.getId());

        assertThat(startDeliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("배달 주문이 아니라면 배달을 시작할 수 없다.")
    void startDelivery_type() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        Order order = Order.ofTakeOut(orderLineItems);

        Order savedOrder = service.create(order);
        Order acceptOrder = service.accept(savedOrder.getId());
        Order serveOrder = service.serve(acceptOrder.getId());

        assertThatThrownBy(() -> service.startDelivery(serveOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("제공 상태가 아니라면 배달을 시작할 수 없다.")
    void startDelivery_satatus() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        String address = "주소";
        Order order = Order.ofDelivery(orderLineItems, address);

        Order savedOrder = service.create(order);
        Order acceptOrder = service.accept(savedOrder.getId());

        assertThatThrownBy(() -> service.startDelivery(acceptOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("`주문`을 배달 완료할 수 있다.")
    void completeDelivery() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        String address = "주소";
        Order order = Order.ofDelivery(orderLineItems, address);

        Order savedOrder = service.create(order);

        Order acceptOrder = service.accept(savedOrder.getId());
        Order serveOrder = service.serve(acceptOrder.getId());
        Order startDeliveryOrder = service.startDelivery(serveOrder.getId());
        Order completeDeliveryOrder = service.completeDelivery(startDeliveryOrder.getId());

        assertThat(completeDeliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("배달 중 상태가 아니라면 배달을 완료 할 수 없다.")
    void completeDelivery_status() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        String address = "주소";
        Order order = Order.ofDelivery(orderLineItems, address);

        Order savedOrder = service.create(order);
        assertThatThrownBy(() -> service.completeDelivery(savedOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("`주문`을 완료할 수 있다.")
    void complete() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        String address = "주소";
        Order order = Order.ofDelivery(orderLineItems, address);

        Order savedOrder = service.create(order);

        Order acceptOrder = service.accept(savedOrder.getId());
        Order serveOrder = service.serve(acceptOrder.getId());
        Order startDeliveryOrder = service.startDelivery(serveOrder.getId());
        Order completeDeliveryOrder = service.completeDelivery(startDeliveryOrder.getId());
        Order completeOrder = service.complete(completeDeliveryOrder.getId());

        assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }


    @Test
    @DisplayName("배달 주문일 때 배달 완료 상태가 아니라면 주문을 완료할 수 없다.")
    void complete_delivery_status() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        String address = "주소";
        Order order = Order.ofDelivery(orderLineItems, address);

        Order savedOrder = service.create(order);
        Order acceptOrder = service.accept(savedOrder.getId());

        assertThatThrownBy(() -> service.complete(acceptOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }


    @Test
    @DisplayName("포장 주문일 때 제공 상태가 아니라면 주문을 완료할 수 없다.")
    void complete_takeout_serve() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        Order order = Order.ofTakeOut(orderLineItems);

        Order savedOrder = service.create(order);
        Order acceptOrder = service.accept(savedOrder.getId());
        Order serveOrder = service.serve(acceptOrder.getId());

        Order complete = service.complete(serveOrder.getId());

        assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(complete.getType()).isEqualTo(OrderType.TAKEOUT);

    }
    @Test
    @DisplayName("매장 주문일 때 제공 상태가 아니라면 주문을 완료할 수 없다.")
    void complete_eat_it_serve() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        OrderTable orderTable = orderTableRepository.save(createOrderTable("테이블1"));
        Order order = Order.ofEatIt(orderLineItems, orderTable);

        Order savedOrder = service.create(order);
        Order acceptOrder = service.accept(savedOrder.getId());
        Order serveOrder = service.serve(acceptOrder.getId());

        Order complete = service.complete(serveOrder.getId());

        assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(complete.getType()).isEqualTo(OrderType.EAT_IN);

    }

    @Test
    @DisplayName("매장 주문이라면 `주문 테이블`의 자리를 비운다.")
    void complete_order_table() {
        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        OrderTable orderTable = orderTableRepository.save(createOrderTable("테이블1"));
        Order order = Order.ofEatIt(orderLineItems, orderTable);

        Order savedOrder = service.create(order);
        Order acceptOrder = service.accept(savedOrder.getId());
        Order serveOrder = service.serve(acceptOrder.getId());

        Order complete = service.complete(serveOrder.getId());

        assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(complete.getType()).isEqualTo(OrderType.EAT_IN);
        OrderTable orderTable1 = complete.getOrderTable();
        assertThat(orderTable1.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("주문 내역을 조회할 수 있다.")
    void findAll() {

        Menu menu1 = menuRepository.save(createMenu("메뉴 이름1"));
        Menu menu2 = menuRepository.save(createMenu("메뉴 이름2"));

        List<OrderLineItem> orderLineItems = Lists.newArrayList(new OrderLineItem(menu1, 2), new OrderLineItem(menu2, 1));

        Order order = Order.ofTakeOut(orderLineItems);

        Order savedOrder1 = service.create(order);
        Order savedOrder2 = service.create(order);

        List<Order> savedOrders = Lists.list(savedOrder1, savedOrder2);

        List<Order> orders = service.findAll();
        assertThat(orders).containsAll(savedOrders);


    }

    private MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup(name);
        menuGroup.setId(UUID.randomUUID());
        return menuGroup;
    }


    private Product createProduct(String name, long price) {
        Product product = new Product(name, BigDecimal.valueOf(price));
        product.setId(UUID.randomUUID());
        return product;
    }

    private static Menu createMenu(String name, long price, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProducts);

        return menu;
    }

    private Menu createMenu(String name) {
        return createMenu(name, true);
    }


    private Menu createMenu(String name, boolean displayed) {
        MenuGroup menuGroup = createMenuGroup("메뉴 그룹 이름");
        Product product1 = createProduct("상품1", 3000L);
        Product product2 = createProduct("상품2", 4000L);
        Product product3 = createProduct("상품3", 2000L);

        List<MenuProduct> savedMenuProduct = Arrays.asList(new MenuProduct(product1.getId(), 10),
                new MenuProduct(product2.getId(), 10),
                new MenuProduct(product3.getId(), 10));


        Menu menu = createMenu(name, 15000L, menuGroup, savedMenuProduct);
        if(!displayed) {
            menu.hide();
        }

        return menu;
    }


    private OrderTable createOrderTable(String name) {
        return createOrderTable(name, true);
    }


    private OrderTable createOrderTable(String name, boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(occupied);

        return orderTable;
    }

}