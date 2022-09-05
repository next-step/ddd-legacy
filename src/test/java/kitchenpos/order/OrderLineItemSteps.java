package kitchenpos.order;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.menu.MenuSteps.메뉴_생성_요청;
import static kitchenpos.menugroup.MenuGroupSteps.메뉴그룹_생성_요청;
import static kitchenpos.product.ProductSteps.상품_생성_요청;

public class OrderLineItemSteps {
    private static UUID 추천메뉴;
    private static UUID 양념치킨;
    private static UUID 후라이드치킨;

    private static UUID 후라이드_두마리_메뉴;
    private static UUID 양념_두마리_메뉴;
    private static UUID 양념_후라이드_메뉴;

    public static void 메뉴그룹_메뉴_메뉴상품_생성() {
        추천메뉴 = 메뉴그룹_생성_요청("추천메뉴").as(MenuGroup.class).getId();
        양념치킨 = 상품_생성_요청("양념치킨", 19000).as(Product.class).getId();
        후라이드치킨 = 상품_생성_요청("후라이드치킨", 17000).as(Product.class).getId();

        List<MenuProduct> 후라이드치킨_메뉴상품 = List.of(new MenuProduct(2, 후라이드치킨));
        List<MenuProduct> 양념치킨_메뉴상품 = List.of(new MenuProduct(2, 양념치킨));
        List<MenuProduct> 반반치킨_메뉴상품 = List.of(new MenuProduct(1, 후라이드치킨), new MenuProduct(1, 양념치킨));

        Menu 메뉴1번 = new Menu("후라이드+후라이드", BigDecimal.valueOf(19000), true, 후라이드치킨_메뉴상품, 추천메뉴);
        Menu 메뉴2번 = new Menu("양념+양념", BigDecimal.valueOf(19000), true, 양념치킨_메뉴상품, 추천메뉴);
        Menu 메뉴3번 = new Menu("양념+후라이드", BigDecimal.valueOf(20000), false, 반반치킨_메뉴상품, 추천메뉴);
        후라이드_두마리_메뉴 = 메뉴_생성_요청(메뉴1번).as(Menu.class).getId();
        양념_두마리_메뉴 = 메뉴_생성_요청(메뉴2번).as(Menu.class).getId();
        양념_후라이드_메뉴 = 메뉴_생성_요청(메뉴3번).as(Menu.class).getId();
    }

    public static OrderLineItem 후라이드_두마리_2개_주문상품_생성() {
        return new OrderLineItem(2, 후라이드_두마리_메뉴, BigDecimal.valueOf(19000.0));
    }

    public static OrderLineItem 양념_두마리_1개_주문상품_생성() {
        return new OrderLineItem(1, 양념_두마리_메뉴, BigDecimal.valueOf(19000.0));
    }

    public static OrderLineItem 보이지_않는_주문상품_생성() {
        return new OrderLineItem(1, 양념_후라이드_메뉴, BigDecimal.valueOf(20000));
    }

    public static OrderLineItem 음수_개수_주문상품_생성() {
        return new OrderLineItem(-1, 양념_두마리_메뉴, BigDecimal.valueOf(19000.0));
    }
}
