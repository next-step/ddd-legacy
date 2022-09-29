package kitchenpos.application.fakeobject;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class FakeMenuRepository implements MenuRepository {
    private List<Menu> menuList = new ArrayList<>();

    public FakeMenuRepository() {
        for (int i = 1; i <= 5; i++) {
            Menu menu = new Menu();
            menu.setId(UUID.fromString("191fa247-b5f3-4b51-b175-e65db523f75" + i));
            menuList.add(menu);
        }
    }

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

    public void setMenuProductsOnMenu(List<Product> productList) {
        for (int i = 0; i < menuList.size(); i++) {
            Menu menu = menuList.get(i);
            MenuProduct menuProduct = new MenuProduct();
            Product product = productList.get(productList.size() % (i + 1));
            menuProduct.setProductId(product.getId());
            menuProduct.setQuantity(1);
            menuProduct.setProduct(product);
            menu.setMenuProducts(List.of(menuProduct));
            menu.setPrice(product.getPrice());
        }
    }
}
