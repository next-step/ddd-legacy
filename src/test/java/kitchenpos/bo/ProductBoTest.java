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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductBoTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    private Product productExpected;

    @BeforeEach
    void setUp() {
        productExpected = new Product("gogi", BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("상품 등록")
    void createProduct() {
        // give
        given(productDao.save(productExpected))
                .willReturn(productExpected);
        // when
        Product createdProduct = productBo.create(productExpected);
        // then
        assertThat(createdProduct.getName()).isEqualTo("gogi");
        assertThat(createdProduct.getPrice().longValue()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("상품 등록 빈 객체 예외처리")
    void createExceptionByEmptyObject() {
        // give
        Product productEmpty = new Product();
        // when
        assertThatIllegalArgumentException().isThrownBy(() -> productBo.create(productEmpty));
    }

    @Test
    @DisplayName("상품 등록 가격이 0 미만 및 null 예외 처리")
    void createExceptionByProductPrice() {
        // give
        Product productPriceNull = new Product("gogi", null);
        Product productPriceMinus = new Product("gogi", BigDecimal.valueOf(-1));
        // when
        assertThatIllegalArgumentException().isThrownBy(() -> productBo.create(productPriceNull));
        assertThatIllegalArgumentException().isThrownBy(() -> productBo.create(productPriceMinus));
    }

    @Test
    @DisplayName("등록되어진 상품들 확인")
    void getSizeProducts() {
        // give
        given(productDao.findAll())
                .willReturn(Arrays.asList(new Product("a", BigDecimal.valueOf(1)), new Product("b", BigDecimal.valueOf(2))));
        // when
        List<Product> products = productBo.list();
        // then
        assertThat(products.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("등록된 상품이 없을때 상품 보기")
    void getProductsByEmptyList() {
        // give
        given(productDao.findAll())
                .willReturn(Collections.emptyList());
        // when
        List<Product> products = productBo.list();
        // then
        assertThat(products.isEmpty()).isTrue();
    }
}
