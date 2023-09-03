package kitchenpos.testfixture;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import javax.persistence.PersistenceException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FakeMenuGroupRepository implements MenuGroupRepository {
    private final Map<UUID, MenuGroup> data = new ConcurrentHashMap<>();

    @Override
    public MenuGroup save(MenuGroup entity) {
        if (data.putIfAbsent(entity.getId(), entity) != null) {
            throw new PersistenceException("The menu group key already exists.");
        }
        return entity;
    }

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<MenuGroup> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }


}

