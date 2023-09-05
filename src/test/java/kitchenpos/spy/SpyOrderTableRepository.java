package kitchenpos.spy;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.UUID;

public interface SpyOrderTableRepository extends OrderTableRepository {

    default <T extends OrderTable> T save(T orderTable) {
        orderTable.setId(UUID.randomUUID());
        return orderTable;
    }
}
