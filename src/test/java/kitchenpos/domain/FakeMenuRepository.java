package kitchenpos.domain;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FakeMenuRepository implements MenuRepository {
    private final Map<UUID, Menu> menus = new HashMap<>();
    private AtomicLong menuProductSeq = new AtomicLong(1L);

    @Override
    public Menu save(Menu menu) {
        menu.getMenuProducts().forEach(menuProduct -> menuProduct.setSeq(menuProductSeq.getAndIncrement()));
        menus.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(menus.get(id));
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menus.values());
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return menus.values().stream()
                .filter(menu -> ids.contains(menu.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return menus.values().stream()
                .filter(menu -> menu.getMenuProducts().stream()
                        .anyMatch(menuProduct -> menuProduct.getProduct().getId().equals(productId)))
                .collect(Collectors.toList());
    }
}
