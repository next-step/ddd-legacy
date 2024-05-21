package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OrderTableFixture {

    public static final String 테이블명 = "테이블명";
    public static final int 두명 = 2;

    public static OrderTable createTable() {
        return createEmptyTable(테이블명);
    }

    public static @NotNull OrderTable createSittingTable(int numberOfGuests) {
        return createTable(테이블명, true, numberOfGuests);
    }

    public static @NotNull OrderTable createEmptyTable(String name) {
        return createTable(name, false, 0);
    }

    public static @NotNull OrderTable createTable(String name, boolean occupied, int numberOfGuests) {
        final var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }
}
