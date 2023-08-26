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

    public Order createServedOrderByType(OrderType orderType) {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return this.create(orderTable, OrderStatus.WAITING, orderType);
    }

    public Order createStatus(OrderStatus orderStatus) {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return this.create(orderTable, orderStatus, OrderType.EAT_IN);
    }

    public Order createStatusWaiting() {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return this.createStatusWaiting(orderTable);
    }

    public Order createStatusWaiting(OrderTable orderTable) {
        return this.create(orderTable, OrderStatus.WAITING, OrderType.EAT_IN);
    }

    public Order createStatusAccept() {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return this.create(orderTable, OrderStatus.ACCEPTED, OrderType.EAT_IN);
    }

    public Order createServedDeliveryOrder() {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return this.create(orderTable, OrderStatus.SERVED, OrderType.DELIVERY);
    }

    public Order createStatusCompleted(OrderTable orderTable) {
        return this.create(orderTable, OrderStatus.COMPLETED, OrderType.EAT_IN);
    }

    public Order createWaitingDeliveryOrder() {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return this.create(orderTable, OrderStatus.WAITING, OrderType.DELIVERY);
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
