package kitchenpos.integration_test_step;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.test_fixture.OrderTableTestFixture;
import org.springframework.stereotype.Component;

@Component
public class OrderTableIntegrationStep {

    private final OrderTableRepository orderTableRepository;

    public OrderTableIntegrationStep(OrderTableRepository orderTableRepository) {
        this.orderTableRepository = orderTableRepository;
    }

    public OrderTable create() {
        OrderTable orderTable = OrderTableTestFixture.create()
                .getOrderTable();
        return orderTableRepository.save(orderTable);
    }
}
