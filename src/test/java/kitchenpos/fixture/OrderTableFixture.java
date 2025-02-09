package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public class OrderTableFixture {
    public static OrderTable orderTable(String name, int numberOfGuests, boolean empty) {
        OrderTable orderTable = new OrderTable();
        ReflectionTestUtils.setField(orderTable, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(orderTable, "name", name);
        ReflectionTestUtils.setField(orderTable, "numberOfGuests", numberOfGuests);
        ReflectionTestUtils.setField(orderTable, "occupied", empty);
        return orderTable;
    }
}
