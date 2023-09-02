package kitchenpos.testfixture;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class NoOpsJpaRepository<T, ID> implements JpaRepository<T, ID> {


    private void throwException() {
        throwException();
        throw new IllegalStateException("Not yet implements");

    }

    @Override
    public void deleteInBatch(Iterable<T> entities) {
        throwException();
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        throwException();
        return null;
    }

    @Override
    public List<T> findAll() {
        throwException();
        return null;
    }

    @Override
    public List<T> findAll(Sort sort) {
        throwException();
        return null;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        throwException();
        return null;
    }

    @Override
    public long count() {
        throwException();
        return 0;
    }

    @Override
    public void deleteById(ID ID) {
        throwException();
    }

    @Override
    public void delete(T entity) {
        throwException();
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> IDs) {
        throwException();
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        throwException();
    }

    @Override
    public void deleteAll() {
        throwException();
    }

    @Override
    public <S extends T> S save(S entity) {
        throwException();
        return null;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        throwException();
        return null;
    }

    @Override
    public Optional<T> findById(ID ID) {
        throwException();
        return Optional.empty();
    }

    @Override
    public boolean existsById(ID ID) {
        throwException();
        return false;
    }

    @Override
    public void flush() {
        throwException();
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        throwException();
        return null;
    }

    @Override
    public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
        throwException();
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<T> entities) {
        throwException();
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<ID> IDs) {
        throwException();
    }

    @Override
    public void deleteAllInBatch() {
        throwException();
    }

    @Override
    public T getOne(ID ID) {
        throwException();
        return null;
    }

    @Override
    public T getById(ID ID) {
        throwException();
        return null;
    }

    @Override
    public T getReferenceById(ID ID) {
        throwException();
        return null;
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        throwException();
        return Optional.empty();
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        throwException();
        return null;
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        throwException();
        return null;
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        throwException();
        return null;
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        throwException();
        return 0;
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        throwException();
        return false;
    }

    @Override
    public <S extends T, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throwException();
        return null;
    }
}
