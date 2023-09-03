package kitchenpos.testfixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product createProduct(String name, long price) {
        return createProduct(UUID.randomUUID(), name, price);
    }

    public static Product createProduct(UUID id, String name, long price) {
        var product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product copy(Product product) {
        var copiedProduct = new Product();
        copiedProduct.setId(product.getId());
        copiedProduct.setName(product.getName());
        copiedProduct.setPrice(product.getPrice());
        return copiedProduct;
    }
}
