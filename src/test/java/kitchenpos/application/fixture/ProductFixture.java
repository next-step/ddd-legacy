package kitchenpos.application.fixture;

import io.micrometer.common.util.StringUtils;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ProductFixture {

    public static final Product DEFAULT_PRODUCT_1 = setProduct("기본상품1", "5000");
    public static final Product DEFAULT_PRODUCT_2 = setProduct("기본상품2", "5000");

    public static final List<Product> DEFAULT_PRODUCT_LIST = Arrays.asList(DEFAULT_PRODUCT_1, DEFAULT_PRODUCT_2);

    public static Product setProduct(String name, String price) {
        Product product = new Product();

        product.setId(UUID.randomUUID());
        product.setName(name);
        if(!StringUtils.isBlank(price)) {
            product.setPrice(BigDecimal.valueOf(Double.parseDouble(price)));
        }

        return product;
    }



}
