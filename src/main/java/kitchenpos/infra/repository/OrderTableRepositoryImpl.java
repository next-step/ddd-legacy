package kitchenpos.infra.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.springframework.stereotype.Repository;

@Repository
class OrderTableRepositoryImpl implements OrderTableRepository {
    private final OrderTableJpaRepository orderTableJpaRepository;

    public OrderTableRepositoryImpl(OrderTableJpaRepository orderTableJpaRepository) {
        this.orderTableJpaRepository = orderTableJpaRepository;
    }

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return orderTableJpaRepository.findById(id);
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        return orderTableJpaRepository.save(orderTable);
    }

    @Override
    public List<OrderTable> findAll() {
        return orderTableJpaRepository.findAll();
    }
}
