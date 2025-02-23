package kitchenpos.fixture;

import kitchenpos.infra.IdGenerator;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    private static final IdGenerator productIdGenerator = UUID::randomUUID;

    public static Product.Builder aProductRequest(){
        return Product.builder()
                .name("후라이드치킨")
                .price(new BigDecimal(16000));
    }
}
