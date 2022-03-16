package kitchenpos.application.fake.helper;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> elements = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return elements.entrySet().stream()
                .filter(entry -> ids.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID id) {
        return elements.values().stream()
                .filter(it ->
                        it.getMenuProducts().stream()
                                .anyMatch(product -> Objects.equals(product.getProductId(), id))
                ).collect(Collectors.toList());
    }

    @Override
    public Menu save(Menu menu) {
        elements.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(elements.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(elements.values());
    }
}
