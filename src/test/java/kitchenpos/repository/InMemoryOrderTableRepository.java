package kitchenpos.repository;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.*;

/**
 * <pre>
 * kitchenpos.repository
 *      InMemoryOrderTableRepository
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-04-20 오후 10:48
 */

public class InMemoryOrderTableRepository implements OrderTableRepository {

    private final Map<UUID, OrderTable> orderTables = new HashMap<>();

    @Override
    public Optional<OrderTable> findById(UUID orderTableId) {
        return Optional.ofNullable(orderTables.get(orderTableId));
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        orderTables.put(orderTable.getId(), orderTable);
        return orderTable;
    }

    @Override
    public List<OrderTable> findAll() {
        return new ArrayList<>(orderTables.values());
    }
}
