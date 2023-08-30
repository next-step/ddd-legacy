package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.dummy.DummyMenu;
import kitchenpos.dummy.DummyOrder;
import kitchenpos.dummy.DummyOrderTable;
import kitchenpos.fake.FakeRidersClient;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fake.InMemoryOrderRepository;
import kitchenpos.fake.InMemoryOrderTableRepository;
import kitchenpos.infra.RidersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static kitchenpos.dummy.DummyOrder.createOrder;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderServiceTest {

    private OrderRepository orderRepository = new InMemoryOrderRepository();
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private RidersClient ridersClient = new FakeRidersClient();
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, ridersClient);
    }

    @DisplayName("매장 주문을 등록한다.")
    @Test
    void create() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getOrderTable().getId()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING)
        );
    }

    @Test
    void accept() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getOrderTable().getId()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
        );
    }

    @Test
    void serve() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getOrderTable().getId()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED)

        );
    }

    @Test
    void startDelivery() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.DELIVERY, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        orderService.startDelivery(actual.getId());
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getDeliveryAddress()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING)

        );
    }

    @Test
    void completeDelivery() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.DELIVERY, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        orderService.startDelivery(actual.getId());
        orderService.completeDelivery(actual.getId());
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getDeliveryAddress()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED)
        );
    }

    @Test
    void complete() {
        OrderTable orderTable = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request = createOrder(OrderType.DELIVERY, orderTable, createOrderLineItem());
        Order actual = orderService.create(request);
        orderService.accept(actual.getId());
        orderService.serve(actual.getId());
        orderService.startDelivery(actual.getId());
        orderService.completeDelivery(actual.getId());
        orderService.complete(actual.getId());
        assertAll(
                () -> assertNotNull(actual.getId()),
                () -> assertNotNull(actual.getDeliveryAddress()),
                () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED)
        );

    }

    @Test
    void findAll() {
        OrderTable orderTable1 = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request1 = createOrder(OrderType.DELIVERY, orderTable1, createOrderLineItem());
        Order actual1 = orderService.create(request1);

        OrderTable orderTable2 = orderTableRepository.save(DummyOrderTable.createOrderTable(true));
        Order request2 = createOrder(OrderType.DELIVERY, orderTable2, createOrderLineItem());
        Order actual2 = orderService.create(request2);

        List<Order> actual = orderService.findAll();
        assertAll(
                () -> assertNotNull(actual.get(0).getId()),
                () -> assertThat(actual.size()).isSameAs(2)
        );

    }


    private List<OrderLineItem> createOrderLineItem() {
        Menu menu1 = menuRepository.save(DummyMenu.createMenu(true));
        Menu menu2 = menuRepository.save(DummyMenu.createMenu(true));
        return DummyOrder.createOrderLineItem(menu1, menu2);
    }
}