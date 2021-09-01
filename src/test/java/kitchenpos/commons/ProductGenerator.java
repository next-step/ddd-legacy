package kitchenpos.commons;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class ProductGenerator {
    @Autowired
    private ProductService ProductService;

    public Product generate() {
        Product product = this.generateRequest();
        return ProductService.create(product);
    }

    public Product generateRequest() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("product");
        product.setPrice(BigDecimal.valueOf(1000));
        return product;
    }
}
