package kitchenpos.fake;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeOrderTableRepository implements OrderTableRepository {
    List<OrderTable> orderTables = new ArrayList<>();

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return orderTables.stream()
                .filter(it -> id.equals(it.getId()))
                .findFirst();
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        orderTables.add(orderTable);
        return orderTables.get(orderTables.size()-1);
    }

    @Override
    public List<OrderTable> findAll() {
        return this.orderTables;
    }
}
