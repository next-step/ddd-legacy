package kitchenpos.fixture;

import kitchenpos.application.InMemoryOrderTableRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import static java.util.UUID.randomUUID;

public class OrderTableFixture {
    public static OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();

    public static OrderTable 주문테이블() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("주문테이블");
        return orderTable;
    }

    public static OrderTable 주문테이블저장() {
        final OrderTable orderTable = 주문테이블();
        orderTable.setId(randomUUID());
        orderTable.setEmpty(true);
        orderTable.setNumberOfGuests(0);
        return orderTableRepository.save(orderTable);
    }

    public static OrderTable 앉은테이블저장() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(randomUUID());
        orderTable.setEmpty(false);
        return orderTableRepository.save(orderTable);
    }

    public static void 비우기() {
        orderTableRepository = new InMemoryOrderTableRepository();
    }
}
