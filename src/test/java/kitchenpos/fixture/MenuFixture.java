package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    private static final String DEFAULT_NAME = "기본메뉴";
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(2000);

    public static Menu create(String name, BigDecimal price, boolean displayed, MenuGroup menuGroup, List<MenuProduct> menuProducts){
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu create(BigDecimal price, List<MenuProduct> products){
        return create(
                DEFAULT_NAME
                , price
                , true
                , MenuGroupFixture.createDefault()
                , products
        );
    }

    public static Menu create(String name, List<MenuProduct> products){
        return create(
                name
                , DEFAULT_PRICE
                , true
                , MenuGroupFixture.createDefault()
                , products
        );
    }

    public static Menu createDefaultWithNameAndPrice(String name, BigDecimal price){
        return create(
                name
                , price
                , true
                , MenuGroupFixture.createDefault()
                , MenuProductFixture.createDefaults()
        );
    }
}
