package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    OrderService orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    KitchenridersClient kitchenridersClient;

    @BeforeEach
    void setup() {
        this.orderService = new OrderService(orderRepository, menuRepository,orderTableRepository, kitchenridersClient);
    }


    @DisplayName("신규 주문 시 주문 유형이 입력되어야 한다.")
    @Test
    void createOrderType() {
        Order order = createOrder(UUID.randomUUID(),OrderStatus.WAITING,null);

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("주문 유형은 매장식사, 배달, 포장이 있다.")
    @Test
    void validOrderType() {


    }
    @DisplayName("주문 상태는 대기, 승인, 제공, 배달중, 배달완료, 완료가 있다.")
    @Test
    void createOrderStatus() {

    }

    @DisplayName("매장 식사가 아닌 경우 수량이 0 이상이어야 한다.")
    @Test
    void orderQuantity() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),true,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.WAITING,OrderType.TAKEOUT);
        OrderLineItem orderLineItem = createOrderLineItem(-1);
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        List<Menu> menus = List.of(menu);
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);

        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("표시되지 않은 메뉴는 주문할 수 없다.")
    @Test
    void displayMenu() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),false,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.WAITING,OrderType.TAKEOUT);
        OrderLineItem orderLineItem = createOrderLineItem(10);
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        List<Menu> menus = List.of(menu);
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("주문 항목의 가격과 메뉴의 가격이 동일해야한다.")
    @Test
    void menuPrice() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),true,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.WAITING,OrderType.TAKEOUT);
        OrderLineItem orderLineItem = createOrderLineItem(10);
        orderLineItem.setPrice(new BigDecimal(1000));
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        List<Menu> menus = List.of(menu);
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("배달인 경우 배송지가 비어있으면 안된다.")
    @Test
    void deliveryAddress() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),true,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.WAITING,OrderType.DELIVERY);
        OrderLineItem orderLineItem = createOrderLineItem(10);
        orderLineItem.setPrice(new BigDecimal(100));
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        List<Menu> menus = List.of(menu);
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("매장 식사인 경우 주문 테이블이 비어있으면 안된다.")
    @Test
    void orderTable() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),true,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.WAITING,OrderType.EAT_IN);
        OrderTable orderTable = new OrderTable();
        orderTable.setOccupied(false);
        order.setOrderTable(orderTable);
        OrderLineItem orderLineItem = createOrderLineItem(10);
        orderLineItem.setPrice(new BigDecimal(100));
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        List<Menu> menus = List.of(menu);
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class);
    }


    @DisplayName("주문 승인 시 주문 상태는 대기여야 한다.")
    @Test
    void acceptOrderStatus() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),true,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.ACCEPTED,OrderType.DELIVERY);
        OrderLineItem orderLineItem = createOrderLineItem(10);
        orderLineItem.setPrice(new BigDecimal(100));
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        List<Menu> menus = List.of(menu);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("주문 유형이 배달인 경우 가격은 (수량*메뉴 가격)의 합이다.")
    @Test
    void acceptDelivery() {

    }

    @DisplayName("주문 제공시 주문상태가 수락이어야 한다.")
    @Test
    void serveStatus() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),true,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.SERVED,OrderType.DELIVERY);
        OrderLineItem orderLineItem = createOrderLineItem(10);
        orderLineItem.setPrice(new BigDecimal(100));
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        List<Menu> menus = List.of(menu);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("배달 시 주문 유형은 배달이어야한다.")
    @Test
    void deliveryType() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),true,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.SERVED,OrderType.EAT_IN);
        OrderLineItem orderLineItem = createOrderLineItem(10);
        orderLineItem.setPrice(new BigDecimal(100));
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        List<Menu> menus = List.of(menu);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);

    }
    @DisplayName("주문 상태는 제공됨이어야한다.")
    @Test
    void deliveryStatus() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),true,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.WAITING,OrderType.DELIVERY);
        OrderLineItem orderLineItem = createOrderLineItem(10);
        orderLineItem.setPrice(new BigDecimal(100));
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        List<Menu> menus = List.of(menu);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("주문 상태는 배달중이어야한다.")
    @Test
    void completeDeliveryStatus() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),true,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.SERVED,OrderType.EAT_IN);
        OrderLineItem orderLineItem = createOrderLineItem(10);
        orderLineItem.setPrice(new BigDecimal(100));
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        List<Menu> menus = List.of(menu);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("주문 유형이 배달이고, 상태가 배달완료여야한다.")
    @Test
    void completeOrderStatus() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),true,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.WAITING,OrderType.DELIVERY);
        OrderLineItem orderLineItem = createOrderLineItem(10);
        orderLineItem.setPrice(new BigDecimal(100));
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        List<Menu> menus = List.of(menu);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);

    }
    @DisplayName("포장이거나, 매장 식사는 상태가 제공됨 이어야한다.")
    @Test
    void completeOrderEatIn() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),true,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.WAITING,OrderType.EAT_IN);
        OrderLineItem orderLineItem = createOrderLineItem(10);
        orderLineItem.setPrice(new BigDecimal(100));
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        List<Menu> menus = List.of(menu);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("매장 식사 시 완료 처리 되지 않은 주문이 없는 경우 손님수를 0으로 설정하고, 테이블 상태를 비어있음으로 변경한다.")
    @Test
    void tableClean() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        Menu menu = createMenu(UUID.randomUUID(),new MenuGroup(),"test",new BigDecimal(100),List.of(menuProduct),true,UUID.randomUUID());
        Order order = createOrder(UUID.randomUUID(),OrderStatus.SERVED,OrderType.EAT_IN);
        OrderTable orderTable = new OrderTable();
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(3);
        order.setOrderTable(orderTable);
        OrderLineItem orderLineItem = createOrderLineItem(10);
        orderLineItem.setPrice(new BigDecimal(100));
        order.setOrderLineItems(List.of(orderLineItem));
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(orderRepository.existsByOrderTableAndStatusNot(any(),any())).thenReturn(false);

        Order orderResult = orderService.complete(order.getId());
        assertThat(orderResult.getOrderTable().getNumberOfGuests()).isEqualTo(0);
        assertThat(orderResult.getOrderTable().isOccupied()).isFalse();

    }

    public Order createOrder(UUID id, OrderStatus status, OrderType type){
        Order order = new Order();
        order.setId(id);
        order.setOrderTable(null);
        order.setOrderTableId(null);
        order.setOrderDateTime(null);
        order.setOrderLineItems(null);
        order.setDeliveryAddress(null);
        order.setStatus(status);
        order.setType(type);
        return order;
    }

    public OrderLineItem createOrderLineItem(long quantity){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(null);
        orderLineItem.setPrice(null);
        orderLineItem.setSeq(null);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(null);
        return orderLineItem;
    }

    public Menu createMenu(UUID menuGroupId, MenuGroup menuGroup, String name, BigDecimal price,
                           List<MenuProduct> menuProductList, boolean displayed, UUID id) {
        Menu menu = new Menu();
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuGroup(menuGroup);
        menu.setName(name);
        menu.setPrice(price);
        if (!menuProductList.isEmpty()) {
            menu.setMenuProducts(menuProductList);
        }
        menu.setDisplayed(displayed);
        menu.setId(id);
        return menu;
    }

}
