package kitchenpos.helper;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.UUID;

public class OrderTableTestHelper {
    private static OrderTableRepository orderTableRepository;

    public OrderTableTestHelper(OrderTableRepository orderTableRepository) {
        this.orderTableRepository = orderTableRepository;
    }

    public static OrderTable 주문테이블_생성(String name){
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);

        return orderTableRepository.save(orderTable);
    }

    public static OrderTable 특정_주문테이블_사용여부_변경(UUID orderTableId, boolean occupied){
        OrderTable orderTable = orderTableRepository.findById(orderTableId)
                .orElseThrow(() -> new IllegalArgumentException("can't find orderTable"));
        orderTable.setOccupied(occupied);

        return orderTableRepository.save(orderTable);
    }

    public static OrderTable 특정_주문테이블_사용인원_변경(UUID orderTableId, int numberOfGuests){
        OrderTable orderTable = orderTableRepository.findById(orderTableId)
                .orElseThrow(() -> new IllegalArgumentException("can't find orderTable"));
        orderTable.setNumberOfGuests(numberOfGuests);

        return orderTableRepository.save(orderTable);
    }
}
