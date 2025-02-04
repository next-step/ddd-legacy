package kitchenpos.application.fake;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.*;
import java.util.stream.Collectors;

public class FakeMenuRepository implements MenuRepository {
    Map<UUID, Menu> memory = new HashMap<>();
    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return memory.values().stream()
                .anyMatch(menu -> ids.contains(menu.getId())) ? new ArrayList<>(memory.values()) : Collections.emptyList();
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(memory.get(id));
    }

    @Override
    public Menu save(Menu menu) {
        memory.put(menu.getId(), menu);
        return memory.get(menu.getId());
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public List<Menu> findAllByProductId(UUID id) {
        List<Menu> result = new ArrayList<>();
        memory.values()
                .forEach(menu -> menu.getMenuProducts()
                        .stream()
                        .anyMatch(product -> product.getProductId().equals(id) ? result.add(menu) : false));
        return result;
    }
}
