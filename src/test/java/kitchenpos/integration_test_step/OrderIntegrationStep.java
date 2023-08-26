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

    public OrderIntegrationStep(OrderRepository orderRepository, MenuIntegrationStep menuIntegrationStep) {
        this.orderRepository = orderRepository;
        this.menuIntegrationStep = menuIntegrationStep;
    }

    public Order create() {
        Menu menu = menuIntegrationStep.create();
        OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                .changeMenu(menu)
                .changePrice(menu.getPrice())
                .getOrderLineItem();
        Order order = OrderTestFixture.create()
                .changeOrderLineItems(Collections.singletonList(orderLineItem))
                .getOrder();
        return orderRepository.save(order);
    }

    public Order createStatusWaiting(OrderTable orderTable) {
        Menu menu = menuIntegrationStep.create();
        OrderLineItem orderLineItem = OrderLineItemTestFixture.create()
                .changeMenu(menu)
                .changePrice(menu.getPrice())
                .getOrderLineItem();
        Order order = OrderTestFixture.create()
                .changeOrderLineItems(Collections.singletonList(orderLineItem))
                .changeOrderTable(orderTable)
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
                .changeStatus(OrderStatus.COMPLETED)
                .getOrder();
        return orderRepository.save(order);
    }
}
