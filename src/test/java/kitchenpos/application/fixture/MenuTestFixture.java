package kitchenpos.application.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuTestFixture {
    public Menu createMenu(MenuGroup menuGroup, String name, BigDecimal price, boolean displayed) {
        Menu menu = new Menu();
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuGroup(menuGroup);
        menu.setName(name);
        menu.setPrice(price);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        menu.setMenuProducts(List.of(menuProduct));

        menu.setDisplayed(displayed);
        menu.setId(UUID.randomUUID());
        return menu;
    }

    public Menu createMenu(MenuProduct menuProduct, BigDecimal price) {
        Menu menu = new Menu();
        menu.setPrice(price);
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuGroup(new MenuGroup());
        menu.setDisplayed(true);
        if(menuProduct!=null){
            menu.setMenuProducts(List.of(menuProduct));
        }
        menu.setId(UUID.randomUUID());
        return menu;
    }


    public Menu createMenu() {
        Menu menu = new Menu();
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuGroup(new MenuGroup());
        menu.setName("testMenu");
        menu.setPrice(BigDecimal.valueOf(10000L));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        menu.setMenuProducts(List.of(menuProduct));

        menu.setDisplayed(true);
        menu.setId(UUID.randomUUID());
        return menu;
    }

    public Menu createMenu(boolean displayed) {
        Menu menu = new Menu();
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuGroup(new MenuGroup());
        menu.setName("testMenu");
        menu.setPrice(BigDecimal.valueOf(10000L));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        menu.setMenuProducts(List.of(menuProduct));

        menu.setDisplayed(displayed);
        menu.setId(UUID.randomUUID());
        return menu;
    }

    public Menu createMenu(BigDecimal price) {
        Menu menu = new Menu();
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuGroup(new MenuGroup());
        menu.setName("testMenu");
        menu.setPrice(price);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        menu.setMenuProducts(List.of(menuProduct));

        menu.setDisplayed(true);
        menu.setId(UUID.randomUUID());
        return menu;
    }

    public Menu createMenu(BigDecimal price, long quantity) {
        Menu menu = new Menu();
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuGroup(new MenuGroup());
        menu.setName("testMenu");
        menu.setPrice(price);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        menuProduct.setQuantity(quantity);
        menu.setMenuProducts(List.of(menuProduct));

        menu.setDisplayed(true);
        menu.setId(UUID.randomUUID());
        return menu;
    }

    public MenuProduct createMenuProduct(String name, BigDecimal price, long quantity) {
        MenuProduct menuProduct = new MenuProduct();

        Product product = new Product();
        product.setPrice(price);
        product.setName(name);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    public MenuProduct createMenuProduct(BigDecimal price, long quantity) {
        MenuProduct menuProduct = new MenuProduct();

        Product product = new Product();
        product.setPrice(price);
        product.setName("testprice");
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    public MenuProduct createMenuProduct(long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
