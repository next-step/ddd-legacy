package kitchenpos.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.db.OrderTableJpaRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderTableRepositoryImpl implements OrderTableRepository {

    private final OrderTableJpaRepository orderTableJpaRepository;

    public OrderTableRepositoryImpl(OrderTableJpaRepository orderTableJpaRepository) {
        this.orderTableJpaRepository = orderTableJpaRepository;
    }

    @Override
    public List<OrderTable> findAll() {
        return orderTableJpaRepository.findAll();
    }

    @Override
    public OrderTable save(OrderTable entity) {
        return orderTableJpaRepository.save(entity);
    }

    @Override
    public Optional<OrderTable> findById(UUID uuid) {
        return orderTableJpaRepository.findById(uuid);
    }
}
