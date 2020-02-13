package kitchenpos;

import kitchenpos.bo.ProductBo;
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
public class ProductBoTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setPrice(BigDecimal.valueOf(1000));
        product.setName("gogi");
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
}
