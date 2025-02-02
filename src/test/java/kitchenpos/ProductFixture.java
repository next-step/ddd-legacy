package kitchenpos;

import helper.IdGenerator;
import helper.PriceGenerator;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    private static final IdGenerator menuIdGenerator = UUID::randomUUID;
    private static final PriceGenerator priceGenerator = BigDecimal::new;

    public static Product 후라이드_치킨_상품(){
        Product product = new Product();
        product.setId(menuIdGenerator.ramdom());
        product.setName("후라이드 치킨");
        product.setPrice(priceGenerator.of(16000));
        return product;
    }

    public static Product 콜라_상품() {
        Product product = new Product();
        product.setId(menuIdGenerator.ramdom());
        product.setName("콜라");
        product.setPrice(priceGenerator.of(1000));
        return product;
    }
}
