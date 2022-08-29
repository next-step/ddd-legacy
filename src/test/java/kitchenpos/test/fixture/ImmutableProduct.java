package kitchenpos.test.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

class ImmutableProduct extends Product {

    ImmutableProduct() {
        super.setId(UUID.randomUUID());
        super.setName("후라이드");
        super.setPrice(BigDecimal.valueOf(16_000));
    }

    @Override
    public void setId(UUID id) {
        throw new IllegalAccessError();
    }

    @Override
    public void setName(String name) {
        throw new IllegalAccessError();
    }

    @Override
    public void setPrice(BigDecimal price) {
        throw new IllegalAccessError();
    }
}
