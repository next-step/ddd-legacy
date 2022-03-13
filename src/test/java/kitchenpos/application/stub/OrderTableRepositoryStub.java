package kitchenpos.application.stub;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

public class OrderTableRepositoryStub implements OrderTableRepository {
    private Map<UUID, OrderTable> orderTables = new HashMap<>();

    @Override
    public List<OrderTable> findAll() {
        return null;
    }

    @Override
    public List<OrderTable> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<OrderTable> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<OrderTable> findAllById(Iterable<UUID> uuids) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(UUID uuid) {

    }

    @Override
    public void delete(OrderTable entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends OrderTable> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends OrderTable> S save(S entity) {
        orderTables.put(entity.getId(), entity);
        return (S) orderTables.get(entity.getId());
    }

    @Override
    public <S extends OrderTable> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<OrderTable> findById(UUID uuid) {
        return Optional.ofNullable(orderTables.get(uuid));
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends OrderTable> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends OrderTable> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<OrderTable> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public OrderTable getOne(UUID uuid) {
        return null;
    }

    @Override
    public OrderTable getById(UUID uuid) {
        return null;
    }

    @Override
    public <S extends OrderTable> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends OrderTable> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends OrderTable> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends OrderTable> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends OrderTable> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends OrderTable> boolean exists(Example<S> example) {
        return false;
    }
}
