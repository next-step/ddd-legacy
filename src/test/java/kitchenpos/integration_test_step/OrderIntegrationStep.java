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

    public Order create() {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        Menu menu = menuIntegrationStep.create();
        OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                .changeMenu(menu)
                .changePrice(menu.getPrice())
                .getOrderLineItem();
        Order order = OrderTestFixture.create()
                .changeOrderTable(orderTable)
                .changeOrderTableId(orderTable)
                .changeOrderLineItems(Collections.singletonList(orderLineItem))
                .getOrder();
        return orderRepository.save(order);
    }

    public Order createStatusAccept() {
        OrderTable orderTable = orderTableIntegrationStep.createSitTable();
        return createStatusAccept(orderTable);
    }

    public Order createStatusAccept(OrderTable orderTable) {
        Menu menu = menuIntegrationStep.create();
        OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                .changeMenu(menu)
                .changePrice(menu.getPrice())
                .getOrderLineItem();
        Order order = OrderTestFixture.create()
                .changeOrderLineItems(Collections.singletonList(orderLineItem))
                .changeOrderTable(orderTable)
                .changeOrderTableId(orderTable)
                .changeStatus(OrderStatus.WAITING)
                .getOrder();
        return orderRepository.save(order);
    }

    public Order createStatusCompleted(OrderTable orderTable) {
        Menu menu = menuIntegrationStep.create();
        OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                .changeMenu(menu)
                .changePrice(menu.getPrice())
                .getOrderLineItem();
        Order order = OrderTestFixture.create()
                .changeOrderLineItems(Collections.singletonList(orderLineItem))
                .changeOrderTable(orderTable)
                .changeOrderTableId(orderTable)
                .changeStatus(OrderStatus.COMPLETED)
                .getOrder();
        return orderRepository.save(order);
    }
}
