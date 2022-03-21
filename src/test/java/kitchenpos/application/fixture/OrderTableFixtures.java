package kitchenpos.application.fixture;

import java.util.UUID;
import kitchenpos.domain.OrderTable;

public final class OrderTableFixtures {

    private OrderTableFixtures() {
        throw new RuntimeException("생성할 수 없는 클래스");
    }

    public static OrderTable createOrderTable(
        UUID id,
        String name,
        int numberOfGuests,
        boolean empty
    ) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }

    public static OrderTable createOrderTable(
        String name,
        int numberOfGuests,
        boolean empty
    ) {
        return createOrderTable(
            UUID.randomUUID(),
            name,
            numberOfGuests,
            empty
        );
    }
}
