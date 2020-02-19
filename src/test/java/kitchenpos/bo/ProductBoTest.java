package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import kitchenpos.dao.ProductDao;
import kitchenpos.product.supports.ProductDaoWithCollection;
import kitchenpos.model.Product;
import kitchenpos.product.supports.ProductDaoWithConstraint;

class ProductBoTest {

    private static final long VALID_ID = 1L;
    private static final String VALID_NAME = "음식";
    private static final BigDecimal VALID_PRICE = BigDecimal.ONE;

    @Test
    @DisplayName("상품을 생성한다.")
    void create() {
        ProductBo cut = new ProductBo(ProductDaoWithConstraint.withCollection(Collections.emptyList()));
        Product product = productOf(VALID_ID, VALID_NAME, VALID_PRICE);

        assertThat(cut.create(product))
            .isEqualTo(product);
    }

    @MethodSource("create_invalid_invariants_cases")
    @ParameterizedTest
    @DisplayName("상품을 생성한다. 불변식 위반")
    void create_when_invariant_is_invalid(String invariantDescription,
                                          Product product,
                                          ProductDao productDao,
                                          Class<Throwable> expected) {
        ProductBo cut = new ProductBo(productDao);

        assertThatThrownBy(() -> cut.create(product))
            .as(invariantDescription)
            .isExactlyInstanceOf(expected);
    }

    private static Stream<Arguments> create_invalid_invariants_cases() {
        return Stream.of(Arguments.of("상품 생성 시 가격은 필수이다.",
                                      productOf(VALID_NAME,
                                                null),
                                      new ProductDaoWithCollection(Collections.emptyList()),
                                      IllegalArgumentException.class),
                         Arguments.of("상품 가격은 0원 이상이다.",
                                      productOf(VALID_NAME,
                                                BigDecimal.ONE.negate()),
                                      new ProductDaoWithCollection(Collections.emptyList()),
                                      IllegalArgumentException.class),
                         Arguments.of("상품 생성 시 상품의 이름은 필수이다.",
                                      productOf(null,
                                                VALID_PRICE),
                                      ProductDaoWithConstraint.withCollection(Collections.emptyList()),
                                      ProductDaoWithConstraint.PRODUCT_CONSTRAINT_EXCEPTION.getClass()));
    }

    static Product productOf(Long id,
                             String name,
                             BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setPrice(price);
        product.setName(name);
        return product;
    }

    static Product productOf(String name,
                             BigDecimal price) {
        return productOf(1L, name, price);
    }
}
