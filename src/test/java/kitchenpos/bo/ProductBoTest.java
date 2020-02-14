package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import kitchenpos.dao.ProductDao;
import kitchenpos.product.supports.CollectionProductDao;
import kitchenpos.model.Product;
import kitchenpos.product.supports.ConstraintProductDao;

class ProductBoTest {

    private static final String VALID_NAME = "음식";
    private static final BigDecimal VALID_PRICE = BigDecimal.ONE;

    @MethodSource("create_invalid_invariants_cases")
    @ParameterizedTest
    @DisplayName("상품을 생성한다. 불변식 위반")
    void create_when_invariant_is_invalid(String invariantDescription,
                                          Product product,
                                          ProductDao productDao,
                                          Class<Throwable> expected) {
        assertThatThrownBy(() -> new ProductBo(productDao).create(product))
            .as(invariantDescription)
            .isExactlyInstanceOf(expected);
    }

    private static Stream<Arguments> create_invalid_invariants_cases() {
        return Stream.of(Arguments.of("상품 생성 시 가격은 필수이다.",
                                      productFrom(VALID_NAME,
                                                  null),
                                      new CollectionProductDao(),
                                      IllegalArgumentException.class),
                         Arguments.of("상품 가격은 0보다 커야한다.",
                                      productFrom(VALID_NAME,
                                                  BigDecimal.ONE.negate()),
                                      new CollectionProductDao(),
                                      IllegalArgumentException.class),
                         Arguments.of("상품 생성 시 상품의 이름은 필수이다.",
                                      productFrom(null,
                                                  VALID_PRICE),
                                      ConstraintProductDao.withCollection(),
                                      ConstraintProductDao.PRODUCT_CONSTRAINT_EXCEPTION.getClass()));
    }

    static Product productFrom(String name,
                               BigDecimal price) {
        Product product = new Product();
        product.setId(1L);
        product.setPrice(price);
        product.setName(name);
        return product;
    }
}