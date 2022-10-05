package kitchenpos.application.fakeobject;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;

import java.util.*;


public class FakeMenuRepository implements MenuRepository {
    private Map<UUID, Menu> menuMap = new HashMap<>();

    public FakeMenuRepository() {
        for (int i = 1; i <= 5; i++) {
            Menu menu = new Menu();
            UUID id = UUID.fromString("191fa247-b5f3-4b51-b175-e65db523f75" + i);
            menu.setId(id);
            menuMap.put(id, menu);
        }
    }

    @Override
    public Menu save(Menu menu) {
        if (menu.getId() != null && menuMap.containsKey(menu.getId())) {
            menuMap.put(menu.getId(), menu);
            return menu;
        }
        menu.setId(UUID.randomUUID());
        menuMap.put(menu.getId(), menu);
        return menu;
    }

    @Override
    public Optional<Menu> findById(UUID menuId) {
        if (menuMap.containsKey(menuId)) {
            return Optional.of(menuMap.get(menuId));
        }
        return Optional.empty();
    }

    @Override
    public List<Menu> findAll() {
        return new ArrayList<>(menuMap.values());
    }

    @Override
    public List<Menu> findAllByIdIn(List<UUID> ids) {
        List<Menu> result = new ArrayList<>();
        for (UUID uuid : ids) {
            if (menuMap.containsKey(uuid)) {
                result.add(menuMap.get(uuid));
            }
        }
        return result;
    }

    @Override
    public List<Menu> findAllByProductId(UUID productId) {
        List<Menu> result = new ArrayList<>();
        for (Menu menu : menuMap.values()) {
            for (MenuProduct menuProduct : menu.getMenuProducts()) {
                if (menuProduct.getProductId().equals(productId)) {
                    result.add(menu);
                    break;
                }
            }
        }
        return result;
    }

    public List<Menu> saveAll(Iterable<Menu> menuList) {
        List<Menu> result = new ArrayList<>();
        for (Menu menu : menuList) {
            result.add(save(menu));
        }
        return result;
    }

    public void setMenuProductsOnMenu(List<Product> productList) {
        for (int i = 0; i < menuMap.values().size(); i++) {
            List<Menu> menuList = new ArrayList<>(menuMap.values());
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
