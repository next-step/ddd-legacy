package kitchenpos.fakeobject;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.UUID;

public class InMemoryOrderTableRepository extends AbstractInMemoryRepository<UUID, OrderTable> implements OrderTableRepository {

}
