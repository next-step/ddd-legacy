package kitchenpos.infra;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.UUID;

@RepositoryDefinition(domainClass = OrderTable.class, idClass = UUID.class)
public interface JpaOrderTableRepository extends OrderTableRepository, JpaSpecificationExecutor<OrderTable> {
}
