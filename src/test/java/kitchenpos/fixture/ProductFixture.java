package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;

public class ProductFixture {

    public static Product.Builder aProductRequest(){
        return Product.builder()
                .name("후라이드치킨")
                .price(new BigDecimal(16000));
    }
}
