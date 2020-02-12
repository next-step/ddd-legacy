package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.mock.ProductBuilder;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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

    @DisplayName("새로운 제품을 생성할 수 있다.")
    @Test
    void create() {
        // given
        Product newProduct = ProductBuilder.mock()
                .withName("제품1")
                .withPrice(BigDecimal.valueOf(1000))
                .build();

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
    @MethodSource(value = "provideInvalidPrice")
    void priceShouldBeOver0(BigDecimal invalidPrice) {
        // given
        Product newProduct = ProductBuilder.mock()
                .withName("제품1")
                .withPrice(invalidPrice)
                .build();

        // when
        // then
        assertThatThrownBy(() -> {
            productBo.create(newProduct);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream provideInvalidPrice() {
        return Stream.of(
            BigDecimal.valueOf(-100),
            BigDecimal.valueOf(-10),
            BigDecimal.valueOf(-1)
        );
    }

    @DisplayName("전체 제품 리스트를 조회할 수 있다.")
    @Test
    void list() {
        // given
        Product product = ProductBuilder.mock()
                .withId(1L)
                .withName("제품1")
                .withPrice(BigDecimal.valueOf(1000))
                .build();
        given(productDao.findAll()).willReturn(Collections.singletonList(product));

        // when
        final List<Product> result = productBo.list();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).containsExactlyInAnyOrder(product);
    }
}
