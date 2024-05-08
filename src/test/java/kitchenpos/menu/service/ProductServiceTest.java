package kitchenpos.menu.service;

import kitchenpos.application.ProductService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.menu.fixture.ProductFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

@DisplayName("상품 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;
    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("새로운 상품을 추가 할 수 있다.")
    void create() {
        Product 떡볶이 = ProductFixture.떡볶이;

        Mockito.when(purgomalumClient.containsProfanity(Mockito.any()))
                        .thenReturn(false);
        Mockito.when(productRepository.save(Mockito.any()))
                .thenReturn(떡볶이);

        Product result = productService.create(떡볶이);

        Assertions.assertThat(result.getName()).isEqualTo(떡볶이.getName());
    }

    @ParameterizedTest
    @MethodSource(value = "providePriceExceptionProduct")
    @DisplayName("상품의 가격은 반드시 존재해야 하며 0보다 커야 한다.")
    void create_exception_상품_가격(Product product) {
        Assertions.assertThatThrownBy(
                () -> productService.create(product)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<Product> providePriceExceptionProduct() {
        return Stream.of(ProductFixture.가격_없는_상품, ProductFixture.가격_음수_상품);
    }

    @Test
    @DisplayName("상품의 이름은 반드시 존재해야 하며 부적절한지 검사한다.")
    void create_exception_상품_이름() {
        Assertions.assertThatThrownBy(
                () -> productService.create(ProductFixture.부적절한_이름_상품)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
