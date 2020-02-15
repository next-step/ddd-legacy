package kitchenpos.menu;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import kitchenpos.model.MenuGroup;
import kitchenpos.model.MenuGroupBuilder;
import kitchenpos.model.Product;
import kitchenpos.model.ProductBuilder;

public class MenuFixtures {

    public static final BigDecimal PRICE_OF_EACH_PRODUCT = BigDecimal.ONE;

    private MenuFixtures() { }

    public static List<Product> productFixture() {
        return Arrays.asList(ProductBuilder.aProduct().withPrice(PRICE_OF_EACH_PRODUCT).build(),
                             ProductBuilder.aProduct().withPrice(PRICE_OF_EACH_PRODUCT).build(),
                             ProductBuilder.aProduct().withPrice(PRICE_OF_EACH_PRODUCT).build(),
                             ProductBuilder.aProduct().withPrice(PRICE_OF_EACH_PRODUCT).build());
    }

    public static MenuGroup menuGroupFixture() {
        return MenuGroupBuilder.aMenuGroup()
                               .withName("열마리메뉴")
                               .build();
    }
}
