package kitchenpos.helper;

import java.util.UUID;
import java.util.function.Supplier;
import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static final Supplier<OrderTable> OCCUPIED_TABLE = () -> create("1번 테이블", 1, true);

    public static final Supplier<OrderTable> EMPTY_TABLE = () -> create("2번 테이블", 0, false);

    private static OrderTable create(String name, int numberOfGuests, boolean occupied) {
        var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable request(String name) {
        var request = new OrderTable();
        request.setName(name);
        return request;
    }

    public static OrderTable request(int numberOfGuests) {
        var request = new OrderTable();
        request.setNumberOfGuests(numberOfGuests);
        return request;
    }
}
