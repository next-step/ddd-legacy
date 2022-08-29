package kitchenpos.test.fixture;

import java.util.UUID;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

class ImmutableMenuProduct extends MenuProduct {

    ImmutableMenuProduct(Product product) {
        super.setProduct(product);
        super.setSeq(0L);
        super.setQuantity(1);
    }

    @Override
    public void setSeq(Long seq) {
        throw new IllegalAccessError();
    }

    @Override
    public void setProduct(Product product) {
        throw new IllegalAccessError();
    }

    @Override
    public void setQuantity(long quantity) {
        throw new IllegalAccessError();
    }

    @Override
    public void setProductId(UUID productId) {
        throw new IllegalAccessError();
    }
}
