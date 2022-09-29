package kitchenpos.application.fakeobject;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeOrderTableRepository implements OrderTableRepository {
    private List<OrderTable> orderTableList = new ArrayList<>();

    public FakeOrderTableRepository() {
        for (int i = 1; i <= 5; i++) {
            OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.fromString("3faec3ab-5217-405d-aaa2-804f87697f8" + i));
            orderTableList.add(orderTable);
        }
    }

    @Override
    public Optional<OrderTable> findById(UUID orderTableId) {
        for (OrderTable orderTable : orderTableList) {
            if (orderTableId.equals(orderTable.getId())) {
                return Optional.of(orderTable);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<OrderTable> findAll() {
        return orderTableList;
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        orderTable.setId(UUID.randomUUID());
        orderTableList.add(orderTable);
        return orderTable;
    }
}
