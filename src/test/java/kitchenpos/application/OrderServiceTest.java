package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    private Menu createMenu(boolean displayed) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("신메뉴");
        menuGroupRepository.save(menuGroup);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("간장치킨");
        menu.setPrice(BigDecimal.valueOf(1000));
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(displayed);
        return menuRepository.save(menu);
    }

    @ParameterizedTest
    @NullSource
    void createWithNullType(OrderType type) {
        // given
        Order request = new Order();
        request.setType(type);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(request));
    }

    @Test
    void createWithNoOrderLineItems() {
        // given
        Order request = new Order();
        request.setType(OrderType.TAKEOUT);
        request.setOrderLineItems(null);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(request));
    }

    @Test
    void createWithNonExistentMenu() {
        // given
        Order request = new Order();
        request.setType(OrderType.TAKEOUT);

        OrderLineItem item = new OrderLineItem();
        item.setMenuId(UUID.randomUUID());
        item.setQuantity(1);
        request.setOrderLineItems(List.of(item));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(request));
    }

    @Test
    void createWithNonDisplayedMenu() {
        // given
        Menu menu = createMenu(false);

        Order request = new Order();
        request.setType(OrderType.TAKEOUT);

        OrderLineItem item = new OrderLineItem();
        item.setMenuId(menu.getId());
        item.setPrice(menu.getPrice());
        item.setQuantity(1);
        request.setOrderLineItems(List.of(item));

        // when & then
        assertThrows(IllegalStateException.class, () -> orderService.create(request));
    }

    @Test
    void createDeliveryWithoutAddress() {
        // given
        Menu menu = createMenu(true);

        Order request = new Order();
        request.setType(OrderType.DELIVERY);

        OrderLineItem item = new OrderLineItem();
        item.setMenuId(menu.getId());
        item.setPrice(menu.getPrice());
        item.setQuantity(1);
        request.setOrderLineItems(List.of(item));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(request));
    }

    @Test
    void createSuccessfully() {
        // given
        Menu menu = createMenu(true);

        Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setDeliveryAddress("address");

        OrderLineItem item = new OrderLineItem();
        item.setMenuId(menu.getId());
        item.setPrice(menu.getPrice());
        item.setQuantity(1);
        request.setOrderLineItems(List.of(item));

        // when & then
        Order res = orderService.create(request);
        assertNotNull(res);
    }

    @Test
    void createEatInWithNonExistentTable() {
        // given
        Menu menu = createMenu(true);

        Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderTableId(UUID.randomUUID());

        OrderLineItem item = new OrderLineItem();
        item.setMenuId(menu.getId());
        item.setPrice(menu.getPrice());
        item.setQuantity(1);
        request.setOrderLineItems(List.of(item));

        // when & then
        assertThrows(NoSuchElementException.class, () -> orderService.create(request));
    }

    @Test
    void acceptNonExistentOrder() {
        // when & then
        assertThrows(NoSuchElementException.class,
                () -> orderService.accept(UUID.randomUUID()));
    }

    @Test
    void acceptNonWaitingOrder() {
        // given
        Order order = createOrder(OrderType.TAKEOUT);
        order.setStatus(OrderStatus.ACCEPTED);
        order.setOrderDateTime(LocalDateTime.now());
        orderRepository.save(order);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> orderService.accept(order.getId()));
    }

    @Test
    void acceptSuccessfully() {
        // given
        Order order = createOrder(OrderType.TAKEOUT);
        order.setOrderDateTime(LocalDateTime.now());
        orderRepository.save(order);

        // when
        Order accepted = orderService.accept(order.getId());

        // then
        assertEquals(OrderStatus.ACCEPTED, accepted.getStatus());
    }

    @Test
    void serveNonAcceptedOrder() {
        // given
        Order order = createOrder(OrderType.TAKEOUT);
        order.setOrderDateTime(LocalDateTime.now());
        orderRepository.save(order);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> orderService.serve(order.getId()));
    }

    @Test
    void startDeliveryForNonDeliveryOrder() {
        // given
        Order order = createOrder(OrderType.TAKEOUT);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderDateTime(LocalDateTime.now());
        orderRepository.save(order);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> orderService.startDelivery(order.getId()));
    }

    @Test
    void completeDeliveryForNonDeliveringOrder() {
        // given
        Order order = createOrder(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderDateTime(LocalDateTime.now());
        orderRepository.save(order);

        // when & then
        assertThrows(IllegalStateException.class,
                () -> orderService.completeDelivery(order.getId()));
    }

    private Order createOrder(OrderType type) {
        Menu menu = createMenu(true);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setStatus(OrderStatus.WAITING);

        OrderLineItem item = new OrderLineItem();
        item.setMenu(menu);
        item.setQuantity(1);
        order.setOrderLineItems(List.of(item));

        if (type == OrderType.DELIVERY) {
            order.setDeliveryAddress("Test Address");
        }

        return order;
    }

    @Test
    void completeOrderSuccessfully() {
        // given
        Order order = createOrder(OrderType.TAKEOUT);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderDateTime(LocalDateTime.now());
        orderRepository.save(order);

        // when
        Order completed = orderService.complete(order.getId());

        // then
        assertEquals(OrderStatus.COMPLETED, completed.getStatus());
    }

    @Test
    void completeEatInOrderAndClearTable() {
        // given
        OrderTable table = new OrderTable();
        table.setId(UUID.randomUUID());
        table.setNumberOfGuests(2);
        table.setOccupied(true);
        table.setName("Test Table");
        orderTableRepository.save(table);

        Order order = createOrder(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderTable(table);
        order.setOrderDateTime(LocalDateTime.now());
        orderRepository.save(order);

        // when
        Order completed = orderService.complete(order.getId());

        // then
        assertEquals(OrderStatus.COMPLETED, completed.getStatus());
        assertFalse(completed.getOrderTable().isOccupied());
        assertEquals(0, completed.getOrderTable().getNumberOfGuests());
    }
}
