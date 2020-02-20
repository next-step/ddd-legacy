package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.dao.TestProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ProductBoTest {

    private ProductDao productDao = new TestProductDao();

    private ProductBo productBo;

    private Random random = new Random();

    @BeforeEach
    void setUp() {
        productBo = new ProductBo(productDao);
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void create() {
        // given
        final Product expected = new Product();
        expected.setId(random.nextLong());
        expected.setName("후라이드 치킨");
        expected.setPrice(BigDecimal.valueOf(16_000L));

        // when
        final Product actual = productBo.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getPrice()).isEqualTo(expected.getPrice());
    }

    @DisplayName("상품의 가격이 올바르지 않으면 등록할 수 없다.")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "-10000"})
    void cannotCreate(final BigDecimal price) {
        // given
        final Product expected = new Product();
        expected.setId(random.nextLong());
        expected.setName("후라이드 치킨");
        expected.setPrice(price);

        // when

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(expected));
    }

    @DisplayName("상품 목록을 조회할 수 있다.")
    @Test
    void list() {
        // given
        final Product expected = new Product();
        expected.setId(random.nextLong());
        expected.setName("후라이드 치킨");
        expected.setPrice(BigDecimal.valueOf(16_000L));
        productDao.save(expected);

        // when
        final List<Product> actual = productBo.list();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual).contains(expected);
    }
}
