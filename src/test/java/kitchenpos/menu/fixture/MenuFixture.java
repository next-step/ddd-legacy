package kitchenpos.menu.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static final int 기본_후라이드_가격 = 16000;

    public static Menu 후라이드_치킨_메뉴() {
        Menu menu = new Menu();
        menu.setName("후라이드 치킨");
        menu.setPrice(new BigDecimal(기본_후라이드_가격));
        menu.setMenuGroup(한마리_메뉴_그룹());
        menu.setMenuProducts(Arrays.asList(후라이드_메뉴_상품(1l)));
        return menu;
    }

    public static Menu 후라이드_치킨_메뉴(List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName("후라이드 치킨");
        menu.setPrice(new BigDecimal(기본_후라이드_가격));
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu 후라이드_치킨_메뉴(int price) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("후라이드 치킨");
        menu.setPrice(new BigDecimal(price));
        return menu;
    }

    public static Product 후라이드_상품(int price) {
        Product product = new Product();
        product.setName("후라이드");
        product.setPrice(new BigDecimal(price));
        return product;
    }

    public static Product 후라이드_상품() {
        Product product = new Product();
        product.setName("후라이드");
        product.setPrice(new BigDecimal(기본_후라이드_가격));
        return product;
    }

    public static Menu 후라이드_치킨_메뉴(int price, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName("후라이드 치킨");
        menu.setPrice(new BigDecimal(price));
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu 메뉴(String name, int price, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(new BigDecimal(price));
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static MenuGroup 한마리_메뉴_그룹() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리메뉴");
        return menuGroup;
    }

    public static MenuProduct 후라이드_메뉴_상품(Long quantity) {
        Product 후라이드_상품 = 후라이드_상품();
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(후라이드_상품);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static MenuProduct 후라이드_메뉴_상품(int price, Long quantity) {
        Product 후라이드_상품 = 후라이드_상품(price);
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(후라이드_상품);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static Menu createMenu(int price) {
        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(price));
        return menu;
    }

    public static Menu createMenu(int price, List<MenuProduct> menuProducts) {
        Menu menu = createMenu(price);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu createMenu(String name, int price, List<MenuProduct> menuProducts) {
        Menu menu = createMenu(price, menuProducts);
        menu.setName(name);
        return menu;
    }

    public static Menu createMenu(String name, int price, boolean displayed, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = createMenu(name, price, menuProducts);
        menu.setDisplayed(displayed);
        menu.setMenuGroup(menuGroup);
        return menu;
    }

    public static MenuGroup createMenuGroup(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuProduct createMenuProduct(Product product, Long quantity, UUID productId) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(productId);
        return menuProduct;
    }
}
