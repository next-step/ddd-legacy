package kitchenpos.fake;

import kitchenpos.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FakeMenuRepository implements MenuRepository {

    private List<Menu> menus = new ArrayList();

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        return this.menus.stream()
                .filter(it -> ids.contains(it.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        return this.menus.stream()
                .filter(it -> hasProduct(it.getMenuProducts(), productId))
                .collect(Collectors.toList());
    }

    private boolean hasProduct(List<MenuProduct> menuProducts, UUID productId) {
        return menuProducts.stream().anyMatch(it -> productId.equals(it.getProductId()));
    }

    @Override
    public Menu save(Menu menu) {
        this.menus.add(menu);
        return menus.get(this.menus.size()-1);
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return this.menus.stream()
                .filter(it -> id.equals(it.getId()))
                .findFirst();
    }

    @Override
    public List<Menu> findAll() {
        return this.menus;
    }
}
