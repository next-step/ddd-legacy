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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductBoTests {
    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    private Product mockProduct;

    @BeforeEach
    public void setup() {
        mockProduct = new Product();
        mockProduct.setPrice(BigDecimal.valueOf(10000));
        mockProduct.setName("testProduct");
    }

    @DisplayName("가격, 이름이 있는 상품 생성 시도 시 성공")
    @Test
    public void createProductSuccess() {
        given(productDao.save(mockProduct)).willReturn(mockProduct);

        Product product = productBo.create(mockProduct);

        assertThat(product.getName()).isEqualTo("testProduct");
    }

    @DisplayName("가격이 없는 상품 생성 시도 시 실패")
    @Test
    public void createProductFailWithoutPrice() {
        mockProduct.setPrice(null);

        assertThatThrownBy(() -> productBo.create(mockProduct)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("음수 가격으로 상품 생성 시도 시 실패")
    @Test
    public void createProductFailWithInvalidPrice() {
        mockProduct.setPrice(BigDecimal.valueOf(-10000));

        assertThatThrownBy(() -> productBo.create(mockProduct)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 전체 조회 시 상품 관련 정보 조회 가능")
    @Test
    public void getProductListSuccess() {
        given(productDao.findAll()).willReturn(Collections.singletonList(mockProduct));

        List<Product> products = productBo.list();

        assertThat(products.get(0).getName()).isEqualTo("testProduct");
    }
}
