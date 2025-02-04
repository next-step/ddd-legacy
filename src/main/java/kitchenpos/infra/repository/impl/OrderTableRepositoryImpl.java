package kitchenpos.infra.repository.impl;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderTableRepositoryImpl implements OrderTableRepository {
    private final OrderTableRepository orderTableRepository;

    public OrderTableRepositoryImpl(OrderTableRepository orderTableRepository) {
        this.orderTableRepository = orderTableRepository;
    }

    @Override
    public OrderTable save(OrderTable orderTable) {
        return orderTableRepository.save(orderTable);
    }

    @Override
    public Optional<OrderTable> findById(UUID id) {
        return orderTableRepository.findById(id);
    }

    @Override
    public List<OrderTable> findAll() {
        return orderTableRepository.findAll();
    }
}
