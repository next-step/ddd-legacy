package kitchenpos.utils;

import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

import static kitchenpos.fixture.ProductFixture.*;

public class ProductTestRepository implements ProductRepository {
    private final Map<UUID, Product> data = new HashMap<>();

    public ProductTestRepository() {
        this.data.put(상품A.getId(), 상품A);
        this.data.put(상품B.getId(), 상품B);
        this.data.put(상품C.getId(), 상품C);
    }

    @Override
    public List<Product> findAllByIdIn(List<UUID> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        return data.values()
                .stream()
                .filter(product -> ids.contains(product.getId()))
                .toList();
    }

    @Override
    public Optional<Product> findById(UUID uuid) {
        return Optional.ofNullable(data.get(uuid));
    }

    // Not Use
    @Override
    public void flush() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Product> S saveAndFlush(S entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Product> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllInBatch(Iterable<Product> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllInBatch() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Product getOne(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Product getById(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Product getReferenceById(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Product> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Product> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Product> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Product> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Product> long count(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Product> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Product, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Product> S save(S entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends Product> List<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean existsById(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Product> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Product> findAllById(Iterable<UUID> uuids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long count() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteById(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Product entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Iterable<? extends Product> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Product> findAll(Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        throw new UnsupportedOperationException();
    }
}
