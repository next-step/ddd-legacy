package kitchenpos.domain;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class StubMenuGroupRepository implements MenuGroupRepository {
    private Map<UUID, MenuGroup> menuGroups = new HashMap<>();

    @Override
    public <S extends MenuGroup> S save(S entity) {
        menuGroups.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<MenuGroup> findById(UUID uuid) {
        final MenuGroup menuGroup = menuGroups.get(uuid);
        return Optional.ofNullable(menuGroup);
    }

    @Override
    public List<MenuGroup> findAll() {
        return menuGroups.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuGroup> findAll(Sort sort) {
        return null;
    }

    @Override
    public List<MenuGroup> findAllById(Iterable<UUID> uuids) {
        return null;
    }

    @Override
    public <S extends MenuGroup> List<S> saveAll(Iterable<S> entities) {
        return null;
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
    public <S extends MenuGroup> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends MenuGroup> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public Page<MenuGroup> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
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
    public <S extends MenuGroup> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
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
}
