package kitchenpos.util;

import java.util.UUID;
import kitchenpos.domain.OrderTable;

public class OrderTableBuilder {

    private UUID id;
    private String name;
    private int numberOfGuests;
    private boolean occupied;

    private OrderTableBuilder() {
    }

    public static OrderTableBuilder id(UUID id) {
        final OrderTableBuilder orderTableBuilder = new OrderTableBuilder();
        orderTableBuilder.id = id;
        return orderTableBuilder;
    }

    public OrderTableBuilder name(String name) {
        this.name = name;
        return this;
    }

    public OrderTableBuilder numberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
        return this;
    }

    public OrderTableBuilder occupied(boolean occupied) {
        this.occupied = occupied;
        return this;
    }

    public OrderTable build() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }
}
