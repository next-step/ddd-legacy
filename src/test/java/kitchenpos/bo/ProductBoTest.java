package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static kitchenpos.bo.Fixture.friedChicken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {
    @Mock
    private ProductDao productDao;

    private ProductBo productBo;

    private Product expected;

    @BeforeEach
    void setUp() {
        productBo = new ProductBo(productDao);
        expected = friedChicken();
    }

    @DisplayName("상품을 생성할 수 있다.")
    @Test
    void create() {
        // given
        given(productDao.save(any(Product.class))).willReturn(expected);

        // when
        final Product actual = productBo.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    }

    @DisplayName("상품의 가격은 0보다 커야한다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "-100000"})
    void name(final BigDecimal price) {
        // given
        final Product expected = new Product();
        expected.setId(1L);
        expected.setName("후라이드 치킨");
        expected.setPrice(price);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(expected));
    }

    @DisplayName("상품을 검색할 수 있다.")
    @Test
    void list() {
        // given
        given(productDao.findAll())
                .willReturn(Collections.singletonList(expected));

        // when
        List<Product> productList = productBo.list();
        Product product = productList.get(0);

        // then
        assertAll(
                () -> assertThat(product).isNotNull(),
                () -> assertThat(product.getName()).isEqualTo(expected.getName()),
                () -> assertThat(product.getPrice()).isEqualTo(expected.getPrice())
        );
    }

}
