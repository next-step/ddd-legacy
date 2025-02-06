package kitchenpos;

import kitchenpos.infra.IdGenerator;
import helper.PriceGenerator;
import helper.SequenceGenerator;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class MenuFixture {

    private static final IdGenerator menuIdGenerator = UUID::randomUUID;
    private static final PriceGenerator priceGenerator = BigDecimal::new;
    private static final SequenceGenerator sequenceGenerator = () -> ThreadLocalRandom.current().nextLong();

    public static Menu 양념_치킨_메뉴() {
        Menu menu = new Menu();
        menu.setId(menuIdGenerator.ramdom());
        menu.setName("양념 치킨 메뉴");
        menu.setPrice(priceGenerator.of(17000));
        return menu;
    }

    public static Menu 후라이드_치킨_메뉴() {
        Menu menu = new Menu();
        menu.setId(menuIdGenerator.ramdom());
        menu.setName("후라이드 치킨 메뉴");
        menu.setPrice(priceGenerator.of(16000));
        return menu;
    }

    public static Menu 후라이드_치킨_메뉴(MenuGroup menuGroup) {
        Menu menu = 후라이드_치킨_메뉴();
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        return menu;
    }

    public static Menu 후라이드_치킨_메뉴(MenuGroup menuGroup, Product... products) {
        Menu menu = 후라이드_치킨_메뉴(menuGroup);
        addMenuProducts(menu, products);
        return menu;
    }

    private static void addMenuProducts(Menu menu, Product[] products) {
        List<MenuProduct> menuProducts = new ArrayList<>();

        for (Product p : products) {
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setSeq(sequenceGenerator.random());
            menuProduct.setProduct(p);
            menuProduct.setProductId(p.getId());
            menuProduct.setQuantity(1L);

            menuProducts.add(menuProduct);
        }

        menu.setMenuProducts(menuProducts);
    }

    public static Menu 가격만_변경된_메뉴(Menu origin, BigDecimal newPrice) {
        Menu clone = new Menu();
        clone.setId(origin.getId());
        clone.setName(origin.getName());
        clone.setMenuGroup(origin.getMenuGroup());
        clone.setMenuProducts(origin.getMenuProducts());

        clone.setPrice(newPrice);

        return clone;
    }

    public static Menu 구성_상품_가격_총합과_동일한_메뉴(MenuGroup menuGroup, Product... products) {
        Menu menu = 후라이드_치킨_메뉴(menuGroup, products);
        BigDecimal sum = Arrays.stream(products)
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(1L)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        menu.setPrice(sum);
        return menu;
    }

    public static Menu 구성_상품_가격_총합을_초과한_메뉴(MenuGroup menuGroup, Product... products) {
        Menu menu = 후라이드_치킨_메뉴(menuGroup, products);
        BigDecimal sum = Arrays.stream(products)
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(1L)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        menu.setPrice(sum.add(BigDecimal.valueOf(1000)));
        return menu;
    }


}
