package kitchenpos.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kitchenpos.domain.Product;

public final class ProductServiceFixture {

    private ProductServiceFixture() {

    }

    public static Product product() {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("후라이드 한마리");
        product.setPrice(BigDecimal.valueOf(18_000));
        return product;

    }

    public static List<Product> products() {
        List<Product> products = new ArrayList<>();
        products.add(product());
        return products;
    }
}
