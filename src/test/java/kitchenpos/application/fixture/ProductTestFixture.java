package kitchenpos.application.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductTestFixture {

    public Product createProduct(String name, BigDecimal price){
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public Product createProduct(BigDecimal price){
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("testProduct");
        product.setPrice(price);
        return product;
    }

    public Product createProduct(String name){
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(10000L));
        return product;
    }

}

