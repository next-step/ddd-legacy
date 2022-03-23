package kitchenpos.domain;

import java.util.UUID;

import static kitchenpos.domain.ProductFixture.*;

public class MenuProductFixture {

    public static MenuProduct MP_FRIED_CHICKEN =
        MenuProductFixture.builder()
                          .seq(1L)
                          .quantity(1L)
                          .product(FRIED_CHICKEN)
                          .productId(FRIED_CHICKEN.getId())
                          .build();

    public static MenuProduct MP_HONEY_COMBO =
        MenuProductFixture.builder()
                          .seq(2L)
                          .quantity(1L)
                          .product(HONEY_COMBO)
                          .productId(HONEY_COMBO.getId())
                          .build();

    private Long seq;
    private long quantity;
    private Product product;
    private UUID productId;

    public static MenuProductFixture builder() {
        return new MenuProductFixture();
    }

    public MenuProductFixture seq(Long seq) {
        this.seq = seq;
        return this;
    }

    public MenuProductFixture quantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    public MenuProductFixture product(Product product) {
        this.product = product;
        return this;
    }

    public MenuProductFixture productId(UUID productId) {
        this.productId = productId;
        return this;
    }

    public MenuProduct build() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        menuProduct.setProductId(productId);
        return menuProduct;
    }
}
