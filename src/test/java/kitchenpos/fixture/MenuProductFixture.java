package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.concurrent.atomic.AtomicLong;

public class MenuProductFixture {

    public static final long SINGLE_QUANTITY = 1L;
    private static final AtomicLong ATOMIC_LONG = new AtomicLong(1L);

    private MenuProductFixture() {
    }

    public static MenuProduct menuProduct(final Product product) {
        return menuProduct(createMenuProductSeq(), SINGLE_QUANTITY, product);
    }

    public static MenuProduct menuProduct(final Long seq, final long quantity, final Product product) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    public static Long createMenuProductSeq() {
        return ATOMIC_LONG.getAndIncrement();
    }
}
