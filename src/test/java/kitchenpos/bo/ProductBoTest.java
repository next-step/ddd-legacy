package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

/**
 * @author Geonguk Han
 * @since 2020-02-13
 */
@ExtendWith(MockitoExtension.class)
class ProductBoTest extends Fixtures {

    @InjectMocks
    private ProductBo productBo;

    @Mock
    private ProductDao productDao;

    @BeforeEach
    void init() {
    }

    @Test
    @DisplayName("상품을 등록한다.")
    void productBo_create() {

        final Product product = products.get(0);

        given(productDao.save(product)).willReturn(product);

        final Product result = productBo.create(product);

        Assertions.assertThat(result.getId()).isEqualTo(product.getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "-8000"})
    @DisplayName("상품을 등록 경계값 검증")
    void productBo_create_validation(final BigDecimal price) {
        final Product product = new Product();
        product.setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(product));
    }

    @Test
    @DisplayName("상품 목록 조회")
    public void productBo_list() {
        given(productBo.list()).willReturn(products);

        final List<Product> result = productBo.list();

        Assertions.assertThat(result.size()).isEqualTo(products.size());
    }
}