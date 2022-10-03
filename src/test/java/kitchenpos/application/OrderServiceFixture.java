package kitchenpos.application;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.OrderFixture.createDeliveryOrder;
import static kitchenpos.fixture.OrderFixture.createEatInOrder;
import static kitchenpos.fixture.OrderFixture.createTakeOutOrder;

import java.math.BigDecimal;
import java.util.Collections;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fake.FakeKitchenridersClient;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fake.InMemoryOrderRepository;
import kitchenpos.fake.InMemoryOrderTableRepository;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;

public class OrderServiceFixture {
    protected static MenuRepository menuRepository;
    protected static OrderRepository orderRepository;
    protected static OrderTableRepository orderTableRepository;
    protected static KitchenridersClient kitchenridersClien;
    protected static OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        kitchenridersClien = new FakeKitchenridersClient();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository,
                kitchenridersClien);
    }


    protected Order 주문접수된_배달주문() {
        Order createdOrder = 배달주문이_등록되어_있음();
        return orderService.accept(createdOrder.getId());
    }

    protected Order 주문제공된_배달주문() {
        Order createdOrder = 주문접수된_배달주문();
        return orderService.serve(createdOrder.getId());
    }

    protected Order 주문을_주문제공_상태까지_진행(Order order) {
        orderService.accept(order.getId());
        return orderService.serve(order.getId());
    }

    protected Order 배달시작된_배달주문() {
        Order createdOrder = 주문제공된_배달주문();
        return orderService.startDelivery(createdOrder.getId());
    }

    protected Order 배달완료된_배달주문() {
        Order createdOrder = 배달시작된_배달주문();
        return orderService.completeDelivery(createdOrder.getId());
    }

    protected Order 매장주문이_등록되어_있음() {
        Order request = createEatInOrder();
        return createdOrder(request);
    }

    protected Order 배달주문이_등록되어_있음() {
        Order request = createDeliveryOrder();
        request.setDeliveryAddress("배달 주소");
        return createdOrder(request);
    }

    protected Order 포장주문이_등록되어_있음() {
        Order request = createTakeOutOrder();
        return createdOrder(request);
    }

    protected Order createdOrder(Order request) {
        request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));
        OrderTable orderTable = createOrderTable();
        request.setOrderTable(orderTable);
        request.setOrderTableId(orderTable.getId());
        return orderService.create(request);
    }

    protected OrderTable createOrderTable() {
        OrderTable request = new OrderTable();
        request.setOccupied(true);
        return orderTableRepository.save(request);
    }

    protected OrderLineItem createOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Menu menu = menuRepository.save(createMenu());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.ONE);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(BigDecimal.ONE);
        orderLineItem.setQuantity(10);
        return orderLineItem;
    }

    protected OrderLineItem createOrderLineItem(long quantity) {
        OrderLineItem orderLineItem = createOrderLineItem();
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }
}
