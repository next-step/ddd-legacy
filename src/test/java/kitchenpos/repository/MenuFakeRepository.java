package kitchenpos.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

public class MenuFakeRepository implements MenuRepository {

    private final List<Menu> menus = new ArrayList<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menus.stream()
            .filter(it -> ids.contains(it.getId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.stream()
            .filter(it -> it.getMenuProducts().stream().anyMatch(menuProduct -> menuProduct.getProductId().equals(it)))
            .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAll() {
        return Collections.unmodifiableList(menus);
    }

    @Override
    public List<Menu> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Menu> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Menu> findAllById(Iterable<UUID> uuids) {
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
    public void delete(Menu entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends Menu> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Menu> S save(S entity) {
        boolean isExist = menus.stream()
            .anyMatch(it -> entity.getId().equals(it.getId()));
        if (isExist) {
            throw new IllegalArgumentException();
        }

        menus.add(entity);
        return entity;
    }

    @Override
    public <S extends Menu> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Menu> findById(UUID uuid) {
        return menus.stream()
            .filter(it -> uuid.equals(it.getId()))
            .findAny();
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Menu> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Menu> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Menu> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Menu getOne(UUID uuid) {
        return null;
    }

    @Override
    public Menu getById(UUID uuid) {
        return null;
    }

    @Override
    public Menu getReferenceById(UUID uuid) {
        return null;
    }

    @Override
    public <S extends Menu> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Menu> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Menu> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Menu> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Menu> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Menu> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Menu, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}
