package kitchenpos.fake;

import kitchenpos.bo.ProductBo;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class FakeProductBoTest {
    private static final long PRODUCT_ID_ONE = 1L;

    private ProductDao productDao = new FakeProductDao();

    private ProductBo productBo;

    @BeforeEach
    void setUp() {
        productBo = new ProductBo(productDao);
    }

    @DisplayName("상품을 등록 할 수 있다")
    @Test
    void create() {
        //given
        Product expected = new Product();
        expected.setId(PRODUCT_ID_ONE);
        expected.setName("치킨");
        expected.setPrice(BigDecimal.valueOf(16_000L));

        //when
        Product actual = productBo.create(expected);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    }

    @DisplayName("상품 가격이 올바르지 않으면 등록 할 수 없다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "-10000"})
    void name(BigDecimal price) {
        //given
        Product expected = new Product();
        expected.setId(PRODUCT_ID_ONE);
        expected.setName("치킨");
        expected.setPrice(price);

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(expected));
    }
}