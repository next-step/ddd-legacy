package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OrderTableFixture {

    public static final String 테이블명 = "테이블명";
    public static final int 두명 = 2;

    public static OrderTable createTable() {
        return createTable(테이블명);
    }

    public static @NotNull OrderTable createTable(String 테이블이름) {
        return createTable(테이블이름, false, 0);
    }

    public static @NotNull OrderTable createTable(String 테이블이름, boolean occupied, int numberOfGuests) {
        final var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(테이블이름);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }
}
