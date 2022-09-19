package kitchenpos.fakeobject;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryOrderTableRepository implements OrderTableRepository {

    @Override
    public Optional<OrderTable> findById(UUID orderTableId) {
        return Optional.empty();
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        return null;
    }

    @Override
    public List<OrderTable> findAll() {
        return null;
    }
}
