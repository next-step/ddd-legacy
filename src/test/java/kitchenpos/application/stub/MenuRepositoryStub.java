package kitchenpos.application.stub;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

public class MenuRepositoryStub implements MenuRepository {

    private Map<UUID, Menu> menus = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return null;
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values().stream()
                .filter(menu -> anyMatch(menu.getMenuProducts(), productId))
                .collect(Collectors.toList());
    }

    private boolean anyMatch(List<MenuProduct> menuProducts, UUID productId) {
        return menuProducts.stream()
                .anyMatch(menuProduct -> menuProduct.getProductId().equals(productId));
    }

    @Override
    public List<Menu> findAll() {
        return null;
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
        menus.put(entity.getId(), entity);
        return (S) menus.get(entity.getId());
    }

    @Override
    public <S extends Menu> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Menu> findById(UUID uuid) {
        return Optional.ofNullable(menus.get(uuid));
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
}
