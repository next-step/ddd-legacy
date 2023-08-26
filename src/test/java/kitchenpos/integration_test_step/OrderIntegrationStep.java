package kitchenpos.integration_test_step;

import kitchenpos.domain.*;
import kitchenpos.test_fixture.OrderTestFixture;
import org.springframework.stereotype.Component;

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
        Order order = OrderTestFixture.create()
                .changeMenu(menu)
                .getOrder();
        return orderRepository.save(order);
    }

    public Order createStatusWaiting(OrderTable orderTable) {
        Menu menu = menuIntegrationStep.create();
        Order order = OrderTestFixture.create()
                .changeMenu(menu)
                .changeOrderTable(orderTable)
                .changeStatus(OrderStatus.WAITING)
                .getOrder();
        return orderRepository.save(order);
    }

    public Order createStatusCompleted(OrderTable orderTable) {
        Menu menu = menuIntegrationStep.create();
        Order order = OrderTestFixture.create()
                .changeMenu(menu)
                .changeOrderTable(orderTable)
                .changeStatus(OrderStatus.COMPLETED)
                .getOrder();
        return orderRepository.save(order);
    }
}
