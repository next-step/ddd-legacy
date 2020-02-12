package kitchenpos.bo;

import kitchenpos.dao.ProductDao;
import kitchenpos.model.Product;
import kitchenpos.support.ProductBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ProductBoTest extends MockTest {
    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductBo sut;

    @Test
    @DisplayName("상품 생성")
    void createProduct() {
        // given
        final Product product = ProductBuilder.product()
                .withName("product name")
                .withPrice(BigDecimal.ONE)
                .build();
        final Product savedProduct = ProductBuilder.product()
                .withId(1L)
                .withName(product.getName())
                .withPrice(product.getPrice())
                .build();
        given(productDao.save(any(Product.class)))
                .willReturn(savedProduct);

        // when
        sut.create(product);

        // then
        verify(productDao).save(any(Product.class));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("provideIllegalPrice")
    @DisplayName("상품의 가격이 없거나 0보다 작으면 예외 던짐")
    void shouldThrowExceptionWithIllegalPrice(BigDecimal price) {
        // given
        final Product product = ProductBuilder.product()
                .withName("product name")
                .withPrice(price)
                .build();

        // when
        assertThatThrownBy(() -> sut.create(product))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        verifyNoInteractions(productDao);
    }

    private static Stream provideIllegalPrice() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-1L)),
                Arguments.of(BigDecimal.valueOf(-12L))
        );
    }

    @Test
    @DisplayName("상품 목록 조회")
    void getProducts() {
        // given
        given(productDao.findAll()).willReturn(
                Collections.singletonList(ProductBuilder.product().build()));

        // when
        sut.list();

        // then
        verify(productDao).findAll();
    }
}
