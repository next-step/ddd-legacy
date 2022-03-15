package kitchenpos.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FakeMenuRepository implements MenuRepository {

    private final Map<UUID, Menu> elements = new HashMap<>();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return elements.values().stream()
            .filter(it -> ids.contains(it.getId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return elements.values().stream()
            .filter(it ->
                it.getMenuProducts().stream()
                    .anyMatch(a -> productId.equals(a.getProductId()))
            ).collect(Collectors.toList());
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(elements.get(id));
    }

    @Override
    public Menu save(Menu menu) {
        return elements.put(menu.getId(), menu);
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(elements.values());
    }
}
