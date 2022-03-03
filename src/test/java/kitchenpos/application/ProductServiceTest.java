package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;

    private static Stream<BigDecimal> providePriceForNullAndNegative() { // argument source method
        return Stream.of(
                null,
                BigDecimal.valueOf(-1000L)
        );
    }

    @DisplayName("상품등록 - 상품의 가격은 반드시 0보다 큰 값을 가져야 한다.")
    @MethodSource("providePriceForNullAndNegative")
    @ParameterizedTest
    public void create01(BigDecimal input) {
        //given
        Product product = new Product();
        product.setPrice(input);
        //when & then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품등록 - 상품은 반드시 이름을 가진다.")
    @Test
    public void create02() {
        //given
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(1000l));
        //when & then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품등록 - 상품의 이름은 비속어를 포함할 수 없다.")
    @Test
    public void create03() {
        //given
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(1000l));
        String slang = "X나 맛없는 미트파이";
        product.setName(slang);
        when(purgomalumClient.containsProfanity(slang))
                .thenReturn(Boolean.TRUE);
        //when & then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품등록 - 상품을 등록할 수 있다.")
    @Test
    public void create04() {
        //given
        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(1000l));
        String name = "맛있는 미트파이";
        request.setName(name);
        when(purgomalumClient.containsProfanity(name))
                .thenReturn(Boolean.FALSE);
        //when
        productService.create(request);

        // & then
        verify(productRepository).save(any(Product.class));
    }
}