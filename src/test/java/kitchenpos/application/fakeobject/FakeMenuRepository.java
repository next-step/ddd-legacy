package kitchenpos.application.fakeobject;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class FakeMenuRepository implements MenuRepository {
    private List<Menu> menuList = new ArrayList<>();

    @Override
    public Menu save(Menu menu) {
        menu.setId(UUID.randomUUID());
        menuList.add(menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        for (Menu menu : menuList) {
            if (menu.getId().equals(menuId)) {
                return Optional.of(menu);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Menu> findAll() {
        return menuList;
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        List<Menu> result = new ArrayList<>();
        for (Menu menu : menuList) {
            if (ids.contains(menu.getId())) {
                result.add(menu);
            }
        }
        return result;
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        List<Menu> result = new ArrayList<>();
        for (Menu menu : menuList) {
            for (MenuProduct menuProduct : menu.getMenuProducts()) {
                if (menuProduct.getProductId().equals(productId)) {
                    result.add(menu);
                    break;
                }
            }
        }
        return result;
    }
}
