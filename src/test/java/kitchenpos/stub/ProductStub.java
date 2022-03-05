package kitchenpos.stub;

import kitchenpos.domain.Product;

import java.math.BigDecimal;

public class ProductStub {

    public static final String THOUSAND_PRICE_PRODUCT_NAME = "1000원상품";
    public static final String TWO_THOUSAND_PRICE_PRODUCT_NAME = "2000원상품";
    public static final String NEGATIVE_THOUSAND_PRICE_PRODUCT_NAME = "-1000원상품";

    private ProductStub() {
    }

    public static Product generateThousandPriceProduct() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(1000));
        product.setName(THOUSAND_PRICE_PRODUCT_NAME);
        return product;
    }

    public static Product generateTwoThousandPriceProduct() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(2000));
        product.setName(TWO_THOUSAND_PRICE_PRODUCT_NAME);
        return product;
    }

    public static Product generateNegativePriceProduct() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(-1000));
        product.setName(NEGATIVE_THOUSAND_PRICE_PRODUCT_NAME);
        return product;
    }
}
