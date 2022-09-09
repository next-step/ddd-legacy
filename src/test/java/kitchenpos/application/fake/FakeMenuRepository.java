package kitchenpos.application.fake;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuRepository;

import java.util.*;
import java.util.stream.Collectors;

class FakeMenuRepository implements MenuRepository {
    private Map<UUID, Menu> fakePersistence = new HashMap<>();

    @Override
    public Menu save(Menu menu) {
        if (fakePersistence.containsKey(menu.getId())) {
            throw new IllegalArgumentException("duplicate primary key");
        }
        return fakePersistence.put(menu.getId(), menu);
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(fakePersistence.get(id));
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return fakePersistence.keySet()
                .stream()
                .filter(id -> ids.contains(id))
                .map(id -> fakePersistence.get(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return fakePersistence.values()
                .stream()
                .filter(menu -> menu.getMenuProducts().stream().filter(menuProduct -> menuProduct.getProductId().equals(productId)).findAny().isPresent())
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAll() {
        return fakePersistence.values()
                .stream()
                .collect(Collectors.toList());
    }
}
