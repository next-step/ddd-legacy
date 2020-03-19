package kitchenpos.model;

import static kitchenpos.model.MenuTest.HALF_AND_HALF_SET_MENU_ID;
import static kitchenpos.model.ProductTest.HALF_CHILLY_ID;
import static kitchenpos.model.ProductTest.HALF_FRIED_ID;

public class MenuProductTest {
    private static final Long SINGLE_HALF_FRIED_ID = 1L;
    private static final Long SINGLE_HALF_CHILLY_ID = 21L;

    public static MenuProduct ofHalfFriedProduct() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(SINGLE_HALF_FRIED_ID);
        menuProduct.setMenuId(HALF_AND_HALF_SET_MENU_ID);
        menuProduct.setProductId(HALF_FRIED_ID);
        menuProduct.setQuantity(1);
        return menuProduct;
    }

    public static MenuProduct ofHalfChillyProduct() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(SINGLE_HALF_CHILLY_ID);
        menuProduct.setMenuId(HALF_AND_HALF_SET_MENU_ID);
        menuProduct.setProductId(HALF_CHILLY_ID);
        menuProduct.setQuantity(1);
        return menuProduct;
    }
}
