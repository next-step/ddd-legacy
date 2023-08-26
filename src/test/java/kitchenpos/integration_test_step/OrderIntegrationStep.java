package kitchenpos.integration_test_step;

import kitchenpos.domain.*;
import kitchenpos.test_fixture.OrderLineItemTestFixture;
import kitchenpos.test_fixture.OrderTestFixture;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class OrderIntegrationStep {
    private final OrderRepository orderRepository;
    private final MenuIntegrationStep menuIntegrationStep;
    private final OrderTableIntegrationStep orderTableIntegrationStep;

    public OrderIntegrationStep(OrderRepository orderRepository, MenuIntegrationStep menuIntegrationStep, OrderTableIntegrationStep orderTableIntegrationStep) {
        this.orderRepository = orderRepository;
        this.menuIntegrationStep = menuIntegrationStep;
        this.orderTableIntegrationStep = orderTableIntegrationStep;
    }

    public Order createEatInByStatus(OrderStatus orderStatus) {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return this.create(orderTable, orderStatus, OrderType.EAT_IN);
    }

    public Order createByTypeAndStatus(OrderType orderType, OrderStatus orderStatus) {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return this.create(orderTable, orderStatus, orderType);
    }

    public Order createServedOrderByType(OrderType orderType) {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return this.create(orderTable, OrderStatus.SERVED, orderType);
    }

    public Order createWaitingEatInOrder() {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return this.createWaitingEatInOrder(orderTable);
    }

    public Order createWaitingEatInOrder(OrderTable orderTable) {
        return this.create(orderTable, OrderStatus.WAITING, OrderType.EAT_IN);
    }

    public Order createAcceptEatInOrder() {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return this.create(orderTable, OrderStatus.ACCEPTED, OrderType.EAT_IN);
    }

    public Order createCompletedEatInOrder(OrderTable orderTable) {
        return this.create(orderTable, OrderStatus.COMPLETED, OrderType.EAT_IN);
    }

    public Order createWaitingDeliveryOrder() {
        return this.createDeliveryByStatus(OrderStatus.WAITING);
    }

    public Order createServedDeliveryOrder() {
        return this.createDeliveryByStatus(OrderStatus.SERVED);
    }

    public Order createDeliveredDeliveryOrder() {
        return this.createDeliveryByStatus(OrderStatus.DELIVERED);
    }

    public Order createDeliveryByStatus(OrderStatus orderStatus) {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return this.create(orderTable, orderStatus, OrderType.DELIVERY);
    }

    public Order create(OrderTable orderTable, OrderStatus orderStatus, OrderType orderType) {
        Menu menu = menuIntegrationStep.create();
        OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                .changeMenu(menu)
                .changePrice(menu.getPrice())
                .getOrderLineItem();
        Order order = OrderTestFixture.create()
                .changeOrderLineItems(Collections.singletonList(orderLineItem))
                .changeOrderTable(orderTable)
                .changeOrderTableId(orderTable)
                .changeType(orderType)
                .changeStatus(orderStatus)
                .getOrder();
        return orderRepository.save(order);
    }
}
