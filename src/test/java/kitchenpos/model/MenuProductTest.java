package kitchenpos.model;

import static kitchenpos.model.MenuTest.HALF_AND_HALF_SET_MENU_ID;
import static kitchenpos.model.ProductTest.HALF_CHILLY_ID;
import static kitchenpos.model.ProductTest.HALF_FRIED_ID;

public class MenuProductTest {

    public static MenuProduct ofHalfFriedProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(HALF_AND_HALF_SET_MENU_ID);
        menuProduct.setProductId(HALF_FRIED_ID);
        menuProduct.setQuantity(1);
        menuProduct.setSeq(1L);
        return menuProduct;
    }

    public static MenuProduct ofHalfChillyProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(HALF_AND_HALF_SET_MENU_ID);
        menuProduct.setProductId(HALF_CHILLY_ID);
        menuProduct.setQuantity(1);
        menuProduct.setSeq(2L);
        return menuProduct;
    }
}
