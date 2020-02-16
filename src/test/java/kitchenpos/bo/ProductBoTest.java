package kitchenpos.bo;

import kitchenpos.builder.ProductBuilder;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductBoTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo productBo;


    @DisplayName("제품을 등록할 수 있다")
    @Test
    void createProduct() {
        Product newProduct = new Product();
        newProduct.setId(1L);
        newProduct.setName("후라이드");
        newProduct.setPrice(BigDecimal.valueOf(16000));

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("후라이드");
        savedProduct.setPrice(BigDecimal.valueOf(16000));

        given(productDao.save(newProduct))
                .willReturn(savedProduct);

        assertThat(productBo.create(newProduct))
                .isEqualToComparingFieldByField(savedProduct);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1", "-0.1"})
    @DisplayName("제품의 가격은 존재해야 하며 0보다 커야 한다.")
    void productPriceException(final BigDecimal price) {
        Product newProduct = new ProductBuilder()
                .id(1L)
                .name("후라이드치킨")
                .price(price)
                .build();

        assertThatThrownBy(() -> productBo.create(newProduct))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("제품의 목록을 조회할 수 있다")
    void listProduct() {
        List<Product> productList = newArrayList();

        productList.add(new ProductBuilder()
                .id(1L)
                .name("후라이드")
                .price(BigDecimal.valueOf(16000))
                .build());
        productList.add(new ProductBuilder()
                .id(2L)
                .name("양념치킨")
                .price(BigDecimal.valueOf(16000))
                .build());
        productList.add(new ProductBuilder()
                .id(3L)
                .name("반반치킨")
                .price(BigDecimal.valueOf(16000))
                .build());

        given(productDao.findAll())
                .willReturn(productList);

        assertThat(productBo.list())
                .hasSameSizeAs(productList)
                .isEqualTo(productList);
    }
}