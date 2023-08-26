package kitchenpos.setup;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.springframework.stereotype.Component;

import static kitchenpos.fixture.OrderTableFixture.generateOrderTable;

@Component
public class OrderTableSetup {

    private final OrderTableRepository orderTableRepository;

    public OrderTableSetup(OrderTableRepository orderTableRepository) {
        this.orderTableRepository = orderTableRepository;
    }

    public OrderTable setupOrderTable(final OrderTable orderTable) {
        return orderTableRepository.save(orderTable);
    }

    public OrderTable setupNewOrderTable() {
        return orderTableRepository.save(generateOrderTable());
    }

    public OrderTable setupOccupiedOrderTable() {
        return setupOccupiedOrderTable(0);
    }

    public OrderTable setupOccupiedOrderTable(final int numberOfGuests) {
        return orderTableRepository.save(generateOrderTable(numberOfGuests, true));
    }
}
