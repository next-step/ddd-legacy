package kitchenpos.service;

import java.util.concurrent.atomic.AtomicLong;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {
    private final MenuProduct menuProduct;
    private AtomicLong seq = new AtomicLong();

    public MenuProductFixture() {
        menuProduct = new MenuProduct();
        menuProduct.setSeq(seq.incrementAndGet());
    }

    public static MenuProductFixture builder() {
        return builder(null);
    }

    public static MenuProductFixture builder(Product product) {
        return new MenuProductFixture()
                .product(product)
                .quantity(1L);
    }

    public MenuProductFixture product(Product product) {
        menuProduct.setProduct(product);
        if (product != null) {
            menuProduct.setProductId(product.getId());
        }
        return this;
    }

    public MenuProductFixture quantity(long quantity) {
        menuProduct.setQuantity(quantity);
        return this;
    }

    public MenuProduct build() {
        return menuProduct;
    }
}
