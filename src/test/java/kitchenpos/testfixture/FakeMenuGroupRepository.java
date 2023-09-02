package kitchenpos.testfixture;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FakeMenuGroupRepository
        extends NoOpsJpaRepository<MenuGroup, UUID> implements MenuGroupRepository {
    private final Map<UUID, MenuGroup> data = new ConcurrentHashMap<>();

    @Override
    public List<MenuGroup> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public <S extends MenuGroup> S save(S entity) {
        if (data.putIfAbsent(entity.getId(), entity) != null) {
            throw new PersistenceException("The menu group key already exists.");
        }
        return entity;
    }
}

