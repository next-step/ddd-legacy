package kitchenpos.application;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
   

    public static Product createProductRequest(final String name) {
        return createProductRequest(name,20_000L);
    }

    public static Product createProductRequest() {
        final Product request = new Product();
        request.setName("후라이드");
        return createProductRequest(request.getName(), 16_000L);
    }

    public static Product createProductRequest(final long price) {
        return createProductRequest("후라이드", price);
    }

    public static Product createProductRequest(final String name, final long price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }


    public static Product createProduct(final String name, final long price){
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        return createProduct(product.getId(), name, price);
    }

    public static Product createProduct(final UUID id, final String name, final long price) {
        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }
}
