package kitchenpos.fakeobject;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMenuRepository extends AbstractInMemoryRepository<UUID, Menu> implements MenuRepository {

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return super.maps.values()
                .stream()
                .filter(menu -> ids.contains(menu.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return super.maps.values()
                .stream()
                .filter(menu -> menu.getMenuProducts().stream().anyMatch(menuProduct -> menuProduct.getProduct().getId().equals(productId)))
                .collect(Collectors.toList());
    }
}
