package kitchenpos.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

public class MenuGroupFakeRepository implements MenuGroupRepository {

    private final List<MenuGroup> menuGroups = new ArrayList<>();

    @Override
    public List<MenuGroup> findAll() {
        return Collections.unmodifiableList(menuGroups);
    }

    @Override
    public List<MenuGroup> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<MenuGroup> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<MenuGroup> findAllById(Iterable<UUID> uuids) {
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
    public void delete(MenuGroup entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends MenuGroup> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public MenuGroup save(MenuGroup entity) {
        menuGroups.add(entity);
        return entity;
    }

    @Override
    public <S extends MenuGroup> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<MenuGroup> findById(UUID uuid) {
        return menuGroups.stream()
            .filter(it -> uuid.equals(it.getId()))
            .findFirst();
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends MenuGroup> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends MenuGroup> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<MenuGroup> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public MenuGroup getOne(UUID uuid) {
        return null;
    }

    @Override
    public MenuGroup getById(UUID uuid) {
        return null;
    }

    @Override
    public MenuGroup getReferenceById(UUID uuid) {
        return null;
    }

    @Override
    public <S extends MenuGroup> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends MenuGroup> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends MenuGroup> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends MenuGroup> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends MenuGroup> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends MenuGroup> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends MenuGroup, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}
