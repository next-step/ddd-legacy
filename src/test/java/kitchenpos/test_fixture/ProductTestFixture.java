package kitchenpos.test_fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductTestFixture {
    private Product product;

    private ProductTestFixture(Product product) {
        this.product = product;
    }

    public static ProductTestFixture create() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("테스트 상품");
        product.setPrice(BigDecimal.valueOf(1000));
        return new ProductTestFixture(product);
    }

    public ProductTestFixture changeId(UUID id) {
        Product newProduct = new Product();
        newProduct.setId(id);
        newProduct.setName(product.getName());
        newProduct.setPrice(product.getPrice());
        this.product = newProduct;
        return this;
    }

    public ProductTestFixture changeName(String name) {
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setName(name);
        newProduct.setPrice(product.getPrice());
        this.product = newProduct;
        return this;
    }

    public ProductTestFixture changePrice(BigDecimal price) {
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setName(product.getName());
        newProduct.setPrice(price);
        this.product = newProduct;
        return this;
    }

    public Product getProduct() {
        return this.product;
    }
}
