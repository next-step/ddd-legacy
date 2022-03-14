package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.*;
import static org.mockito.BDDMockito.*;

@DisplayName("상품 서비스")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    private ProductService productService;

    @BeforeEach
    public void setup() {
        this.productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @Test
    @DisplayName("상품 이름은 필수로 있어야 한다.")
    public void create() {
        Product productFixture = ProductFixture.normalProduct();

        given(purgomalumClient.containsProfanity(productFixture.getName())).willReturn(false);
        given(productRepository.save(any())).willReturn(productFixture);

        Product product = productService.create(productFixture);
        Assertions.assertAll(
            () -> assertThat(product.getId()).isEqualTo(productFixture.getId()),
            () -> assertThat(product.getName()).isEqualTo(productFixture.getName()),
            () -> assertThat(product.getPrice()).isEqualTo(productFixture.getPrice())
        );
    }

    @Test
    @DisplayName("상품 이름은 필수로 있어야 한다.")
    public void requiredProductName() {
        Product productFixture = ProductFixture.emptyProductName();

        ThrowingCallable throwingCallable = () -> productService.create(productFixture);

        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품 이름에는 비속어가 포함될 수 없다.")
    public void productNameContainsSlangTest() {
        Product productFixture = ProductFixture.slangProductName();

        Mockito.when(purgomalumClient.containsProfanity(productFixture.getName())).thenReturn(true);

        ThrowingCallable throwingCallable = () -> productService.create(productFixture);

        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품 가격은 0 보다 작으면 안된다.")
    public void productPriceLessThanZero() {
        Product productFixture = ProductFixture.wrongPriceProduct();

        ThrowingCallable throwingCallable = () -> productService.create(productFixture);

        assertThatThrownBy(throwingCallable)
                .isInstanceOf(IllegalArgumentException.class);
    }
}