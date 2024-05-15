package kitchenpos.domain.testfixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;

import java.util.*;

public class MenuFakeRepository implements MenuRepository {

    Map<UUID, Menu> instancePocket;

    public MenuFakeRepository() {
        this.instancePocket = new HashMap<>();
    }

    public Menu save(Menu menu) {
        instancePocket.put(menu.getId(), menu);
        return menu;
    }

    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return instancePocket.values()
                .stream()
                .filter(menu -> ids.contains(menu.getId()))
                .toList();
    }

    public List<Menu> findAllByProductId(UUID id) {
        return instancePocket.values()
                .stream()
                .filter(menu -> hasProduct(id, menu))
                .toList();
    }

    private boolean hasProduct(UUID productId, Menu menu) {
        return menu.getMenuProducts()
                .stream()
                .anyMatch(menuProduct -> hasProduct(productId, menuProduct));
    }

    private boolean hasProduct(UUID productId, MenuProduct menuProduct) {
        return menuProduct.getProduct().getId() == productId;
    }

    public Optional<Menu> findById(UUID id) {
        return Optional.ofNullable(instancePocket.get(id));
    }

    public List<Menu> findAll() {
        return instancePocket.values()
                .stream()
                .toList();
    }
}
