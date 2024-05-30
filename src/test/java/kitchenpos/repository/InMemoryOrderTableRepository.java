package kitchenpos.repository;

import kitchenpos.domain.*;
import kitchenpos.domain.OrderTable;

import java.util.*;

public class InMemoryOrderTableRepository implements OrderTableRepository {
    private final BaseInMemoryDao<OrderTable> dao = new BaseInMemoryDao<>();

    @Override
    public OrderTable save(OrderTable menu) {
        return dao.save(menu);
    }

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return dao.findById(id);
    }

    @Override
    public List<OrderTable> findAll() {
        return dao.findAll();
    }


}
