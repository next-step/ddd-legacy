package kitchenpos.objectmother;

import kitchenpos.domain.Product;

import java.math.BigDecimal;

public class ProductMaker {

    public static final Product 욕설상품 = make("Fuck", 1000L);
    public static final Product 음수가격상품 = make("상품", -1000L);
    public static final Product 상품_1 = make("상품1", 1000L);
    public static final Product 상품_2 = make("상품2", 1500L);

    public static Product make(String name, Long price) {
        return new Product(name, new BigDecimal(price));
    }

}
