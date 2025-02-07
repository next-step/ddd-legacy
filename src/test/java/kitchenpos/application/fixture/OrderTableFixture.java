package kitchenpos.application.fixture;

import kitchenpos.domain.OrderTable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable createOrderTable(String name) {
        return createOrderTable(null, name, 0);
    }

    public static OrderTable createOrderTable(UUID id, String name, int numberOfGuests) {
        return createOrderTable(id, name, false, numberOfGuests);
    }

    public static OrderTable createOrderTable(UUID id, String name, boolean occupied, int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        ReflectionTestUtils.setField(orderTable, "id", id);
        ReflectionTestUtils.setField(orderTable, "name", name);
        ReflectionTestUtils.setField(orderTable, "occupied", occupied);
        ReflectionTestUtils.setField(orderTable, "numberOfGuests", numberOfGuests);
        return orderTable;
    }

    private OrderTableFixture() {
    }
}
