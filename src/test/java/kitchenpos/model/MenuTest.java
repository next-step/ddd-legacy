package kitchenpos.model;

import java.math.BigDecimal;
import java.util.Arrays;

import static kitchenpos.model.MenuGroupTest.SET_MENU_GROUP_ID;

public class MenuTest {
    static final Long HALF_AND_HALF_SET_MENU_ID = 1L;

    public static Menu ofHalfAndHalf() {
        final Menu menu = new Menu();
        menu.setId(HALF_AND_HALF_SET_MENU_ID);
        menu.setMenuGroupId(SET_MENU_GROUP_ID);
        menu.setName("반반세트");
        menu.setPrice(BigDecimal.valueOf(14000));
        menu.setMenuProducts(
                Arrays.asList(MenuProductTest.ofHalfFriedProduct(),
                        MenuProductTest.ofHalfChillyProduct())
        );
        return menu;
    }
}
