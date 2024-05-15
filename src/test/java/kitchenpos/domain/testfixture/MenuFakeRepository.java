package kitchenpos.domain.testfixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MenuFakeRepository implements MenuRepository {

    InstancePocket<UUID, Menu> instancePocket;

    public MenuFakeRepository() {
        this.instancePocket = new InstancePocket<>();
    }

    public Menu save(Menu menu) {
        return instancePocket.save(menu);
    }

    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return instancePocket.findAllByIdIn(ids);
    }

    public List<Menu> findAllByProductId(UUID id) {
        return instancePocket.findAll()
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
        return instancePocket.findById(id);
    }

    public List<Menu> findAll() {
        return instancePocket.findAll();
    }
}
