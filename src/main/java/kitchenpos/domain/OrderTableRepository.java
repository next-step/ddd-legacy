package kitchenpos.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface OrderTableRepository extends CrudRepository<OrderTable, UUID> {
    List<OrderTable> findAll();
}
