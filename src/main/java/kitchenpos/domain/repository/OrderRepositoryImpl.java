package kitchenpos.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.db.OrderJpaRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import org.springframework.stereotype.Service;

@Service
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public boolean existsByOrderTableAndStatusNot(OrderTable orderTable, OrderStatus status) {
        return orderJpaRepository.existsByOrderTableAndStatusNot(orderTable, status);
    }

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll();
    }

    @Override
    public Order save(Order entity) {
        return orderJpaRepository.save(entity);
    }

    @Override
    public Optional<Order> findById(UUID uuid) {
        return orderJpaRepository.findById(uuid);
    }
}
