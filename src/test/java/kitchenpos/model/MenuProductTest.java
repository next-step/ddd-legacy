package kitchenpos.model;

public class MenuProductTest {

    public static MenuProduct ofHalfFriedProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(1L);
        menuProduct.setProductId(ProductTest.ofHalfFried().getId());
        menuProduct.setQuantity(1);
        menuProduct.setSeq(1L);
        return menuProduct;
    }

    public static MenuProduct ofHalfChillyProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(1L);
        menuProduct.setProductId(ProductTest.ofHalfChilly().getId());
        menuProduct.setQuantity(1);
        menuProduct.setSeq(2L);
        return menuProduct;
    }
}