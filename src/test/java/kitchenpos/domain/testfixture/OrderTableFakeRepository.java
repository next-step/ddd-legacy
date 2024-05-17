package kitchenpos.domain.testfixture;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderTableFakeRepository implements OrderTableRepository {

    private final InstancePocket<UUID, OrderTable> instancePocket;

    public OrderTableFakeRepository() {
        this.instancePocket = new InstancePocket<>();
    }

    @Override
    public Optional<OrderTable> findById(UUID orderTableId) {
        return instancePocket.findById(orderTableId);
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        return instancePocket.save(orderTable);
    }

    @Override
    public List<OrderTable> findAll() {
        return instancePocket.findAll();
    }
}
