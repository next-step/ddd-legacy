package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

/**
 * @author Geonguk Han
 * @since 2020-02-13
 */
@ExtendWith(MockitoExtension.class)
class ProductBoTest {

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
        final Product product = new Product();
        product.setId(1l);
        product.setName("짜장면");
        product.setPrice(BigDecimal.valueOf(6000));

        // given
        given(productDao.save(product)).willReturn(product);

        // when
        final Product result = productBo.create(product);

        // then
        Assertions.assertThat(result.getId()).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("상품을 등록 경계값 검증")
    void productBo_create_validation() {
        final Product product = new Product();
        product.setPrice(BigDecimal.valueOf(-1l));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productBo.create(product));
    }

    @Test
    @DisplayName("상품 목록 조회")
    public void productBo_list() {
        // todo: fixture refactoring 대상
        final Product product = new Product();
        product.setId(1l);
        product.setName("짜장면");
        product.setPrice(BigDecimal.valueOf(6000));

        final Product product1 = new Product();
        product1.setId(2l);
        product1.setName("짬봉");
        product1.setPrice(BigDecimal.valueOf(7000));

        final List<Product> products = Arrays.asList(product, product1);

        // given
        given(productBo.list()).willReturn(products);

        // when
        final List<Product> result = productBo.list();

        // then
        Assertions.assertThat(result.size()).isEqualTo(products.size());
    }
}