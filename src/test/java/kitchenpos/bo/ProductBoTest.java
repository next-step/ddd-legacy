package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    @Mock
    ProductDao productDao;

    @InjectMocks
    ProductBo productBo;

    private List<Product> mockProducts;
    private Product newProduct;

    @BeforeEach
    void beforeEach() {
        /**
         * 새로운 제품
         */
        newProduct = new Product();
        newProduct.setName("진짜제품");
        newProduct.setPrice(BigDecimal.valueOf(1000));

        /**
         * 제품 리스트
         */
        mockProducts = new ArrayList<>();

        LongStream.range(0, 100).forEach(i -> {
            Product product = new Product();
            product.setId(i);
            product.setName("제품" + i);
            product.setPrice(BigDecimal.valueOf(1000).multiply(BigDecimal.valueOf(i)));

            mockProducts.add(product);
        });
    }

    @DisplayName("새로운 제품을 생성할 수 있다.")
    @Test
    void create() {
        // given
        given(productDao.save(any(Product.class))).willAnswer(invocation -> {
            newProduct.setId(1L);
            return newProduct;
        });

        // when
        Product result = productBo.create(newProduct);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(newProduct.getName());
        assertThat(result.getPrice()).isEqualTo(newProduct.getPrice());
    }

    @DisplayName("제품 가격은 0원 이상이다.")
    @ParameterizedTest
    @ValueSource(ints = {-100, -10, -1})
    void priceShouldBeOver0(int price) {
        // given
        newProduct.setPrice(BigDecimal.valueOf(price));

        // when
        // then
        assertThatThrownBy(() -> {
            productBo.create(newProduct);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("전체 제품 리스트를 조회할 수 있다.")
    @Test
    void list() {
        // given
        given(productDao.findAll()).willReturn(mockProducts);

        // when
        final List<Product> result = productBo.list();

        // then
        assertThat(result.size()).isEqualTo(mockProducts.size());
        assertThat(result.get(0).getId()).isEqualTo(mockProducts.get(0).getId());
        assertThat(result.get(0).getName()).isEqualTo(mockProducts.get(0).getName());
        assertThat(result.get(0).getPrice()).isEqualTo(mockProducts.get(0).getPrice());
    }
}
