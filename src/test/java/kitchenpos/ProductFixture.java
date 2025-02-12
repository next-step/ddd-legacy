package kitchenpos;

import kitchenpos.infra.IdGenerator;
import helper.PriceGenerator;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    private static final IdGenerator productIdGenerator = UUID::randomUUID;

    public static Product 후라이드_치킨_상품_Request(){
        Product product = new Product();
        product.setName("후라이드 치킨");
        product.setPrice(PriceGenerator.of(16000));
        return product;
    }

    public static Product 후라이드_치킨_상품_Request(BigDecimal price){
        Product product = new Product();
        product.setName("후라이드 치킨");
        product.setPrice(price);
        return product;
    }

    public static Product 콜라_상품_Request() {
        Product product = new Product();
        product.setName("콜라");
        product.setPrice(PriceGenerator.of(1000));
        return product;
    }

    //각격변경
    public static Product 가격만_변경된_상품(Product origin, BigDecimal price) {
        origin.setName("콜라");
        origin.setPrice(price);
        return origin;
    }
}
