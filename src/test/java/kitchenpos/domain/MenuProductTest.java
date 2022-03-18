package kitchenpos.domain;

public class MenuProductTest {
    public static MenuProduct create(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);

        return menuProduct;
    }
}
