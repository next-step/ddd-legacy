package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static Menu 후라이드_한마리메뉴(MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("후라이드치킨");
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static class MenuGroupFixture {
        public static MenuGroup 한마리메뉴() {
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());
            menuGroup.setName("한마리메뉴");
            return menuGroup;
        }

        public static MenuGroup 두마리메뉴() {
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());
            menuGroup.setName("두마리메뉴");
            return menuGroup;
        }
    }

    public static class MenuProductFixture {
        public static MenuProduct 메뉴상품_후라이드(Product product) {
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setSeq(1L);
            menuProduct.setProduct(product);
            menuProduct.setProductId(product.getId());
            menuProduct.setQuantity(1);
            return menuProduct;
        }

        public static MenuProduct 메뉴상품_양념(Product product) {
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setSeq(2L);
            menuProduct.setProduct(product);
            menuProduct.setProductId(product.getId());
            menuProduct.setQuantity(1);
            return menuProduct;
        }

        public static MenuProduct 메뉴상품_양념_재고음수(Product product) {
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setSeq(2L);
            menuProduct.setProduct(product);
            menuProduct.setProductId(product.getId());
            menuProduct.setQuantity(-1);
            return menuProduct;
        }
    }


}
