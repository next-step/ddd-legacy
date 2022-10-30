package kitchenpos.menu.menu;

import kitchenpos.menu.menu.dto.request.MenuProductRequest;
import kitchenpos.menu.menu.dto.request.MenuRequest;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.product.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MenuRequestFixture {

    public static MenuRequest 메뉴가격_NULL(MenuGroup menuGroup, Product product) {
        return new MenuRequest(menuGroup.getId(), "메뉴명", null, 상품수량_메뉴상품_수량_같음(product));
    }

    public static MenuRequest 메뉴상품_NULL(MenuGroup menuGroup, Product product) {
        return new MenuRequest(menuGroup.getId(), "메뉴명", BigDecimal.TEN, null);
    }

    public static MenuRequest 다른메뉴그룹ID(MenuGroup menuGroup, Product product) {
        return new MenuRequest(UUID.randomUUID(), "메뉴명", BigDecimal.TEN, null);
    }

    public static MenuRequest 메뉴(MenuGroup menuGroup, Product product) {
        return new MenuRequest(menuGroup.getId(), "메뉴명", BigDecimal.TEN, 상품수량_메뉴상품_수량_같음(product));
    }

    public static MenuRequest 메뉴가격_음수(MenuGroup menuGroup, Product product) {
        return new MenuRequest(menuGroup.getId(), "메뉴명", BigDecimal.valueOf(-1), 상품수량_메뉴상품_수량_같음(product));
    }

    public static MenuRequest 상품수량_메뉴상품_수량_다름(MenuGroup menuGroup, Product product) {
        return new MenuRequest(menuGroup.getId(), "메뉴명", BigDecimal.TEN, 상품수량_메뉴상품_수량_다름(product));
    }

    public static List<MenuProductRequest> 상품수량_메뉴상품_수량_같음(Product product) {
        List<MenuProductRequest> menuProductRequests = new ArrayList<>();
        MenuProductRequest menuProductRequest = new MenuProductRequest(product.getId(), 1);
        menuProductRequests.add(menuProductRequest);
        return menuProductRequests;
    }

    public static List<MenuProductRequest> 상품수량_메뉴상품_수량_다름(Product product) {
        List<MenuProductRequest> menuProductRequests = new ArrayList<>();
        MenuProductRequest menuProductRequest = new MenuProductRequest(product.getId(), 1);
        menuProductRequests.add(menuProductRequest);
        menuProductRequests.add(menuProductRequest);
        return menuProductRequests;
    }

}
