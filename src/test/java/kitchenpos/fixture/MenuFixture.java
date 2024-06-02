package kitchenpos.fixture;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuFixture {

    public static Menu createRequest(Long price, MenuGroup menuGroup, Product product, Integer quantity) {
        return createRequest("후라이드1+1", price, menuGroup, true, product, quantity);
    }

    public static Menu createRequest(String name, Long price, MenuGroup menuGroup, Product product, Integer quantity) {
        return createRequest(name, price, menuGroup, true, product, quantity);
    }

    public static Menu createRequest(String name, Long price, MenuGroup menuGroup, Boolean displayed, Product product, Integer quantity){
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        if(menuGroup != null){
            menu.setMenuGroupId(menuGroup.getId());
        }
        menu.setDisplayed(displayed);
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        menu.setMenuProducts(List.of(menuProduct));

        return menu;
    }

    public static Menu changePriceRequest(Long price){
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(price));
        return menu;
    }

    public static Menu createFriedOnePlusOne(MenuGroup menuGroup, Product product){
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("후라이드1+1");
        menu.setPrice(BigDecimal.valueOf(30_000L));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(true);
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(2);
        menuProduct.setProductId(product.getId());
        menu.setMenuProducts(List.of(menuProduct));
        return menu;
    }

    public static Menu createSeasoned2(MenuGroup menuGroup, Product product){
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("양념1+1");
        menu.setPrice(BigDecimal.valueOf(35_000L));
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(true);
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(2);
        menuProduct.setProductId(product.getId());
        menu.setMenuProducts(List.of(menuProduct));
        return menu;
    }
}
