package kitchenpos.bo;

import kitchenpos.dao.DefaultProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {
    @Mock private DefaultProductDao productDao;
    @InjectMocks private ProductBo productBo;

    private Product product;
    private List<Product> productList;

    @BeforeEach
    void setup() {
        this.product = new Product();
        this.product.setId(1L);
        this.product.setName("후라이드");
        this.product.setPrice(new BigDecimal(16000));

        this.productList = new ArrayList<>();
        this.productList.add(this.product);
    }

    @DisplayName("상품을 생성할 때 상품의 가격을 반드시 입력해야 한다.")
    @Test
    void createProductWithoutPriceTest() {
        //given
        Product givenProduct = product;
        givenProduct.setPrice(null);

        //when
        //then
        assertThatThrownBy(() ->{ productBo.create(givenProduct); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품을 생성할 때 상품의 가격은 반드시 양수를 입력해야 한다.")
    @Test
    void createProductWithNegativePriceTest() {
        //given
        Product givenProduct = product;
        givenProduct.setPrice(BigDecimal.valueOf(-1));

        //when
        //then
        assertThatThrownBy(() ->{ productBo.create(givenProduct); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 목록을 볼 수 있다.")
    @Test
    void list() {
        //given
        List<Product> givenProductList = productList;
        given(productDao.findAll())
                .willReturn(givenProductList);

        //when
        List<Product> actualProductList = productBo.list();

        //then
        assertThat(actualProductList.size())
                .isEqualTo(givenProductList.size());
    }

}
