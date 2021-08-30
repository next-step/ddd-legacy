package kitchenpos.commons;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductGenerator {
    @Autowired
    private ProductService ProductService;

    public Product generate() {
        Product product = new Product();
        product.setName("product");
        product.setPrice(BigDecimal.valueOf(1000));
        return ProductService.create(product);
    }
}
