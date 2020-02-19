package kitchenpos.model;

import java.math.BigDecimal;
import java.util.Arrays;

public class MenuTest {

    public static final Menu ofHalfAndHalf() {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setMenuGroupId(MenuGroupTest.ofSet().getId());
        menu.setName("반반세트");
        menu.setPrice(BigDecimal.valueOf(14000));
        menu.setMenuProducts(
                Arrays.asList(MenuProductTest.ofHalfFriedProduct(), MenuProductTest.ofHalfChillyProduct())
        );
        return menu;
    }
}