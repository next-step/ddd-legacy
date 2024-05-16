package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.testfixture.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderServiceTest {

    private OrderRepository orderRepository = new InMemoryOrderRepository();
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private KitchenridersClient kitchenridersClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Test
    void create() {

        // given
        Product product = ProductTestFixture.createProduct(
                UUID.randomUUID(),
                "후라이드치킨",
                17000L
        );

        Menu menu = MenuTestFixture.createMenu(
                UUID.randomUUID(),
                "후라이드치킨",
                17000L,
                true,
                product
        );
        menuRepository.save(menu);
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(
                UUID.randomUUID(),
                "1번",
                true,
                2
        );
        orderTableRepository.save(orderTable);

        OrderLineItem orderLineItem = OrderLineItemTestFixture.createOrderLine(1L, 1, menu);

        Order request = OrderTestFixture.createOrderRequest(
                OrderType.EAT_IN,
                OrderStatus.WAITING,
                LocalDateTime.now(),
                orderLineItem,
                orderTable
        );

        //when
        Order response = orderService.create(request);

        //then
        assertThat(response.getId()).isNotNull();

    }

    @Test
    void accept() {

        //given
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(UUID.randomUUID(), "2번", true, 2);
        orderTableRepository.save(orderTable);
        UUID request = UUID.randomUUID();
        Order order = OrderTestFixture.createOrder(request, OrderType.EAT_IN, OrderStatus.WAITING, LocalDateTime.now(), new OrderLineItem(), orderTable);
        orderRepository.save(order);
        //when
        Order response = orderService.accept(request);

        //then
        assertEquals(OrderStatus.ACCEPTED, response.getStatus());

    }

    @Test
    void canNotAcceptWhenDoNotHaveOrder(){
        //given
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(UUID.randomUUID(), "2번", true, 2);
        orderTableRepository.save(orderTable);
        UUID request = UUID.randomUUID();
        Order order = OrderTestFixture.createOrder(request, OrderType.EAT_IN, OrderStatus.WAITING, LocalDateTime.now(), new OrderLineItem(), orderTable);


        //when then
        assertThrows(NoSuchElementException.class, () -> orderService.accept(request));
    }

    @Test
    void canNotAcceptWhenNotWaiting(){
        //given
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(UUID.randomUUID(), "2번", true, 2);
        orderTableRepository.save(orderTable);
        UUID request = UUID.randomUUID();
        Order order = OrderTestFixture.createOrder(request, OrderType.EAT_IN, OrderStatus.WAITING, LocalDateTime.now(), new OrderLineItem(), orderTable);



        //when then
        assertThrows(NoSuchElementException.class, () -> orderService.accept(request));
    }

    @Test
    void serve() {

        //given
        Menu menu = MenuTestFixture.createMenu(
                UUID.randomUUID(),
                "후라이드치킨",
                17000L,
                true,
                ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 17000L)
        );
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(UUID.randomUUID(), "1번", true, 2);
        Order order = OrderTestFixture.createOrder(
                UUID.randomUUID(),
                OrderType.EAT_IN,
                OrderStatus.ACCEPTED,
                LocalDateTime.now(),
                OrderLineItemTestFixture.createOrderLine(1L, 1, menu),
                orderTable
        );
        orderRepository.save(order);

        //when
        Order response = orderService.serve(order.getId());

        //then
        assertEquals(OrderStatus.SERVED, response.getStatus());
    }

    @Test
    void canNotServedWhenNotAccepted(){
        //given
        Menu menu = MenuTestFixture.createMenu(
                UUID.randomUUID(),
                "후라이드치킨",
                17000L,
                true,
                ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 17000L)
        );
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(UUID.randomUUID(), "1번", true, 2);
        Order order = OrderTestFixture.createOrder(
                UUID.randomUUID(),
                OrderType.DELIVERY,
                OrderStatus.WAITING,
                LocalDateTime.now(),
                OrderLineItemTestFixture.createOrderLine(1L, 1, menu),
                orderTable
        );
        orderRepository.save(order);

        //when then
        assertThrows(IllegalStateException.class, () -> orderService.serve(order.getId()));
    }

    @Test
    void startDelivery() {
        //given
        Menu menu = MenuTestFixture.createMenu(
                UUID.randomUUID(),
                "후라이드치킨",
                17000L,
                true,
                ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 17000L)
        );
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(UUID.randomUUID(), "1번", true, 2);
        Order order = OrderTestFixture.createOrder(
                UUID.randomUUID(),
                OrderType.DELIVERY,
                OrderStatus.SERVED,
                LocalDateTime.now(),
                OrderLineItemTestFixture.createOrderLine(1L, 1, menu),
                orderTable
        );
        orderRepository.save(order);

        //when
        Order response = orderService.startDelivery(order.getId());

        //then
        assertEquals(OrderStatus.DELIVERING, response.getStatus());

    }

    @Test
    void canNotStartDeliveryWhenNotServed(){
        //given
        Menu menu = MenuTestFixture.createMenu(
                UUID.randomUUID(),
                "후라이드치킨",
                17000L,
                true,
                ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 17000L)
        );
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(UUID.randomUUID(), "1번", true, 2);
        Order order = OrderTestFixture.createOrder(
                UUID.randomUUID(),
                OrderType.DELIVERY,
                OrderStatus.ACCEPTED,
                LocalDateTime.now(),
                OrderLineItemTestFixture.createOrderLine(1L, 1, menu),
                orderTable
        );
        orderRepository.save(order);

        //when then
        assertThrows(IllegalStateException.class, () -> orderService.startDelivery(order.getId()));
    }


    @Test
    void completeDelivery() {

        //given
        Menu menu = MenuTestFixture.createMenu(
                UUID.randomUUID(),
                "후라이드치킨",
                17000L,
                true,
                ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 17000L)
        );
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(UUID.randomUUID(), "1번", true, 2);
        Order order = OrderTestFixture.createOrder(
                UUID.randomUUID(),
                OrderType.DELIVERY,
                OrderStatus.DELIVERING,
                LocalDateTime.now(),
                OrderLineItemTestFixture.createOrderLine(1L, 1, menu),
                orderTable
        );
        orderRepository.save(order);

        //when
        Order response = orderService.completeDelivery(order.getId());

        //then
        assertEquals(OrderStatus.DELIVERED, response.getStatus());
    }

    @Test
    void canNotCompleteDeliveryWhenNotDelivering(){
        //given
        Menu menu = MenuTestFixture.createMenu(
                UUID.randomUUID(),
                "후라이드치킨",
                17000L,
                true,
                ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 17000L)
        );
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(UUID.randomUUID(), "1번", true, 2);
        Order order = OrderTestFixture.createOrder(
                UUID.randomUUID(),
                OrderType.DELIVERY,
                OrderStatus.ACCEPTED,
                LocalDateTime.now(),
                OrderLineItemTestFixture.createOrderLine(1L, 1, menu),
                orderTable
        );
        orderRepository.save(order);

        //when then
        assertThrows(IllegalStateException.class, () -> orderService.completeDelivery(order.getId()));
    }

    @Test
    void complete() {

        //given
        Menu menu = MenuTestFixture.createMenu(
                UUID.randomUUID(),
                "후라이드치킨",
                17000L,
                true,
                ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 17000L)
        );
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(UUID.randomUUID(), "1번", true, 2);
        Order order = OrderTestFixture.createOrder(
                UUID.randomUUID(),
                OrderType.DELIVERY,
                OrderStatus.DELIVERED,
                LocalDateTime.now(),
                OrderLineItemTestFixture.createOrderLine(1L, 1, menu),
                orderTable
        );
        orderRepository.save(order);

        //when
        Order response = orderService.complete(order.getId());

        //then
        assertEquals(OrderStatus.COMPLETED, response.getStatus());

    }
    @Test
    void canNotCompleteWhenNotDelivered(){
        //given
        Menu menu = MenuTestFixture.createMenu(
                UUID.randomUUID(),
                "후라이드치킨",
                17000L,
                true,
                ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 17000L)
        );
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(UUID.randomUUID(), "1번", true, 2);
        Order order = OrderTestFixture.createOrder(
                UUID.randomUUID(),
                OrderType.DELIVERY,
                OrderStatus.SERVED,
                LocalDateTime.now(),
                OrderLineItemTestFixture.createOrderLine(1L, 1, menu),
                orderTable
        );
        orderRepository.save(order);
        assertThrows(IllegalStateException.class, () -> orderService.complete(order.getId()));
    }

    @Test
    void canNotCompleteWhenNotServed(){
        //given
        Menu menu = MenuTestFixture.createMenu(
                UUID.randomUUID(),
                "후라이드치킨",
                17000L,
                true,
                ProductTestFixture.createProduct(UUID.randomUUID(), "후라이드치킨", 17000L)
        );
        OrderTable orderTable = OrderTableTestFixture.createOrderTable(UUID.randomUUID(), "1번", true, 2);
        Order order = OrderTestFixture.createOrder(
                UUID.randomUUID(),
                OrderType.TAKEOUT,
                OrderStatus.WAITING,
                LocalDateTime.now(),
                OrderLineItemTestFixture.createOrderLine(1L, 1, menu),
                orderTable
        );
        orderRepository.save(order);
        assertThrows(IllegalStateException.class, () -> orderService.complete(order.getId()));
    }

    @Test
    void findAll() {

        // given
        Order order1 = OrderTestFixture.createOrder(
                UUID.randomUUID(),
                OrderType.EAT_IN,
                OrderStatus.ACCEPTED,
                LocalDateTime.now(),
                new OrderLineItem(),
                new OrderTable());
        Order order2 = OrderTestFixture.createOrder(
                UUID.randomUUID(),
                OrderType.TAKEOUT,
                OrderStatus.SERVED,
                LocalDateTime.now(),
                new OrderLineItem(),
                new OrderTable());

        orderRepository.save(order1);
        orderRepository.save(order2);

        // when
        List<Order> response = orderService.findAll();

        // then
        assertEquals(2, response.size());

    }
}