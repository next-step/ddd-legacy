package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductBoTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("gogi", BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("상품 등록")
    void registerProduct() {
        // give
        given(productDao.save(product))
                .willReturn(product);
        // when
        Product createdProduct = productBo.create(product);
        // then
        assertThat(createdProduct.getName()).isEqualTo("gogi");
        assertThat(createdProduct.getPrice().longValue()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("상품 등록 빈 객체 예외처리")
    void registerExceptionByEmptyObject() {
        // give
        Product productEmpty = new Product();
        // when
        assertThatIllegalArgumentException().isThrownBy(() -> productBo.create(productEmpty));
    }

    @Test
    @DisplayName("상품 등록 가격이 0 미만 및 null 예외 처리")
    void registerExceptionByProductPrice() {
        // give
        Product productPriceNull = new Product("gogi", null);
        Product productPriceMinus = new Product("gogi", BigDecimal.valueOf(-1));
        // when
        assertThatIllegalArgumentException().isThrownBy(() -> productBo.create(productPriceNull));
        assertThatIllegalArgumentException().isThrownBy(() -> productBo.create(productPriceMinus));
    }
}
