package kitchenpos;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DummyData {

    protected static List<MenuGroup> menuGroups = new ArrayList<>();
    protected static List<Product> products = new ArrayList<>();
    protected static List<Menu> menus = new ArrayList<>();
    protected static List<MenuProduct> menuProducts = new ArrayList<>();

    protected static final UUID FIRST_ID = UUID.randomUUID();
    protected static final UUID SECOND_ID = UUID.randomUUID();

    protected static final Boolean MENU_HIDE = false;
    protected static final Boolean MENU_SHOW = true;

    public DummyData() {
        setMenuGroups();
        setProducts();
        setMenuProducts();
        setMenus();
    }

    private void setMenuGroups() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(FIRST_ID);
        menuGroup.setName("추천메뉴");

        MenuGroup menuGroup2 = new MenuGroup();
        menuGroup.setId(SECOND_ID);
        menuGroup.setName("중식");

        menuGroups.add(menuGroup);
        menuGroups.add(menuGroup2);
    }

    private void setProducts() {
        Product product = new Product();
        product.setId(FIRST_ID);
        product.setPrice(ofPrice(1000));
        product.setName("후라이드");

        Product product2 = new Product();
        product.setId(SECOND_ID);
        product.setPrice(ofPrice(1100));
        product.setName("양념");

        products.add(product);
        products.add(product2);
    }

    private void setMenuProducts() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setProduct(products.get(0));
        menuProduct.setQuantity(1L);

        MenuProduct menuProduct2 = new MenuProduct();
        menuProduct2.setSeq(2L);
        menuProduct2.setProduct(products.get(1));
        menuProduct2.setQuantity(1L);

        menuProducts.add(menuProduct);
        menuProducts.add(menuProduct2);
    };

    private void setMenus() {
        Menu menu = new Menu();
        menu.setId(FIRST_ID);
        menu.setMenuGroup(menuGroups.get(0));
        menu.setName("점심 한정");
        menu.setPrice(ofPrice(5000));
        menu.setDisplayed(MENU_SHOW);
        menu.setMenuProducts(Arrays.asList(menuProducts.get(0)));

        Menu menu2 = new Menu();
        menu2.setId(SECOND_ID);
        menu2.setMenuGroup(menuGroups.get(1));
        menu2.setName("저녁 한정");
        menu2.setPrice(ofPrice(10000));
        menu2.setDisplayed(MENU_SHOW);
        menu2.setMenuProducts(Arrays.asList(menuProducts.get(1)));

        menus.add(menu);
        menus.add(menu2);
    }

    private BigDecimal ofPrice(int price) {
        return BigDecimal.valueOf(price);
    }
}
