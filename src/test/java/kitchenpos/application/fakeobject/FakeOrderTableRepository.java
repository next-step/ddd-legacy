package kitchenpos.application.fakeobject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeOrderTableRepository implements OrderTableRepository {
    private List<OrderTable> orderTableList = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper();

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
        if (orderTable.getId() != null) {
            for (OrderTable orderTableItem : orderTableList) {
                if (orderTableItem.getId().equals(orderTable.getId())) {
                    try {
                        orderTableItem = objectMapper.readValue(objectMapper.writeValueAsString(orderTable), OrderTable.class);
                        return orderTableItem;
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        orderTable.setId(UUID.randomUUID());
        orderTableList.add(orderTable);
        return orderTable;
    }
}
