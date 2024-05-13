package kitchenpos.utils;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

import static kitchenpos.fixture.MenuFixture.메뉴_그룹A;

public class MenuGroupTestRepository implements MenuGroupRepository {
    private final Map<UUID, MenuGroup> data = new HashMap<>();

    public MenuGroupTestRepository() {
        this.data.put(메뉴_그룹A.getId(), 메뉴_그룹A);
    }

    @Override
    public Optional<MenuGroup> findById(UUID uuid) {
        return Optional.ofNullable(data.get(uuid));
    }

    // Not Use
    @Override
    public void flush() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends MenuGroup> S saveAndFlush(S entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends MenuGroup> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllInBatch(Iterable<MenuGroup> entities) {
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
    public MenuGroup getOne(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MenuGroup getById(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MenuGroup getReferenceById(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends MenuGroup> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends MenuGroup> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends MenuGroup> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends MenuGroup> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends MenuGroup> long count(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends MenuGroup> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends MenuGroup, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends MenuGroup> S save(S entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <S extends MenuGroup> List<S> saveAll(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean existsById(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<MenuGroup> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<MenuGroup> findAllById(Iterable<UUID> uuids) {
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
    public void delete(MenuGroup entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Iterable<? extends MenuGroup> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<MenuGroup> findAll(Sort sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<MenuGroup> findAll(Pageable pageable) {
        throw new UnsupportedOperationException();
    }
}
