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

    @DisplayName("가격, 이름이 있느 상품 생성 시도 시 성공")
    @Test
    public void createProductSuccess() {
        given(productDao.save(mockProduct)).willReturn(mockProduct);

        Product product = productBo.create(mockProduct);

        assertThat(product.getName()).isEqualTo("testProduct");
    }
}
