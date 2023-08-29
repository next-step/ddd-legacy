package kitchenpos.dummy;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class DummyProduct {
    public static Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static Product defaultProduct() {
        return createProduct("테스트 상품", new BigDecimal(10000));
    }

    public static Product createProductRequest(long price) {
        return createProductRequest(BigDecimal.valueOf(price), "후라이드 치킨");
    }

    public static Product createProductRequest(String name) {
        return createProductRequest(BigDecimal.valueOf(16_000L), name);
    }

    public static Product createProductRequest() {
        return createProductRequest(BigDecimal.valueOf(16_000L), "후라이드 치킨");
    }

    public static Product createProductRequest(final UUID id, final BigDecimal price, String name) {
        final Product request = new Product();
        request.setId(id);
        request.setPrice(price);
        request.setName(name);
        return request;
    }

    public static Product createProductRequest(BigDecimal price, String name) {
        final Product request = new Product();
        request.setPrice(price);
        request.setName(name);
        return request;
    }
}
