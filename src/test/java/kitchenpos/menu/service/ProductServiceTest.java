package kitchenpos.menu.service;

import kitchenpos.application.ProductService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.menu.fixture.MenuFixture;
import kitchenpos.menu.fixture.productFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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

    private productFixture productFixture;
    private MenuFixture menuFixture;

    @BeforeEach
    void setUp() {
        productFixture = new productFixture();
        menuFixture = new MenuFixture();
    }

    @Test
    @DisplayName("새로운 상품을 추가 할 수 있다.")
    void create() {
        Product 떡볶이 = productFixture.상품_A;

        Mockito.when(purgomalumClient.containsProfanity(Mockito.any()))
                        .thenReturn(false);
        Mockito.when(productRepository.save(Mockito.any()))
                .thenReturn(떡볶이);

        Product result = productService.create(떡볶이);

        Assertions.assertThat(result.getName()).isEqualTo(떡볶이.getName());
    }

    @Test
    @DisplayName("상품의 가격은 반드시 존재해야 하며 0보다 커야 한다.")
    void create_exception_상품_가격() {
        List<Product> 상품_목록 = List.of(productFixture.가격_없는_상품, productFixture.가격_음수_상품);

        for (Product 상품 : 상품_목록) {
            Assertions.assertThatThrownBy(
                    () -> productService.create(상품)
            ).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    @DisplayName("상품의 이름은 반드시 존재해야 하며 부적절한지 검사한다.")
    void create_exception_상품_이름() {
        Assertions.assertThatThrownBy(
                () -> productService.create(productFixture.부적절한_이름_상품)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 가격을 변경할 수 있다.")
    void changePrice() {
        Product 상품_A = productFixture.상품_A;
        Product 상품_B = productFixture.상품_C;

        Mockito.when(productRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(상품_A));
        Mockito.when(menuRepository.findAllByProductId(Mockito.any()))
                .thenReturn(List.of(menuFixture.메뉴_A));

        productService.changePrice(상품_A.getId(), 상품_B);
        Assertions.assertThat(상품_A.getPrice()).isEqualTo(상품_B.getPrice());
    }

    @Test
    @DisplayName("해당 상품으로 구성된 메뉴의 가격이 변경된 상품의 가격 총합보다 크다면 메뉴를 노출하지 않는다.")
    void changePrice_exception_price() {
        Product 상품_C = productFixture.상품_C;
        Product 상품_A = productFixture.상품_A;

        Mockito.when(productRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(상품_C));
        Mockito.when(menuRepository.findAllByProductId(Mockito.any()))
                .thenReturn(List.of(menuFixture.메뉴_C));

        productService.changePrice(상품_C.getId(), 상품_A);
        Assertions.assertThat(menuFixture.메뉴_C.isDisplayed()).isEqualTo(false);
    }
}
