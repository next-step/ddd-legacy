package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static final Product 상품A = 상품_생성("상품A", BigDecimal.valueOf(10_000));
    public static final Product 상품B = 상품_생성("상품B", BigDecimal.valueOf(20_000));
    public static final Product 상품C = 상품_생성("상품C", BigDecimal.valueOf(30_000));

    public static Product 상품_생성(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static MenuProduct 메뉴_상품_생성(Product product, Long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        return menuProduct;
    }
}
