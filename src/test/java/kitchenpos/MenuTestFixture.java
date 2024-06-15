package kitchenpos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.application.MenuGroupService;
import kitchenpos.application.MenuService;
import kitchenpos.application.ProductService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuTestFixture {
    public static Menu createMenu(String menuName, BigDecimal menuPrice, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setMenuProducts(menuProducts);
        MenuGroup menuGroup = createMenuGroup(menuName);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        return menu;
    }

    public static MenuProduct createMenuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        return menuProduct;
    }

    public static MenuProduct createMenuProductRequest(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static Menu getSavedMenu(ProductService productService, MenuService menuService, MenuGroupService menuGroupService, BigDecimal menuPrice, boolean displayed, BigDecimal productPrice) {
        Product productRequest = new Product();
        productRequest.setName("TestProduct");
        productRequest.setPrice(productPrice);
        Product product = productService.create(productRequest);

        MenuGroup menuGroupRequest = new MenuGroup();
        menuGroupRequest.setName("TestMenuGroup");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        String menuName = "TestMenu";
        Menu menuRequest = new Menu();
        menuRequest.setPrice(menuPrice);
        menuRequest.setName(menuName);
        menuRequest.setMenuGroup(menuGroup);
        menuRequest.setMenuGroupId(menuGroup.getId());
        MenuProduct menuProduct = createMenuProductRequest(product, 1);
        menuRequest.setMenuProducts(List.of(menuProduct));
        menuRequest.setDisplayed(displayed);

        return menuService.create(menuRequest);
    }
}
