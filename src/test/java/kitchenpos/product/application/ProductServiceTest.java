package kitchenpos.product.application;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.menu.fixture.MenuFixture.createMenu;
import static kitchenpos.product.fixture.ProductionFixture.createMenuProduct;
import static kitchenpos.product.fixture.ProductionFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Product 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    PurgomalumClient purgomalumClient;

    ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품 가격이 음수거나 null인 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-100})
    void createWithNegativePrice(int price) {
        // given
        Product product = createProduct("강정치킨", price);

        // when, then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 이름에 욕설을 포함된 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(strings = {"fuck","shit"})
    void createWithoutBadWords(String name) {
        // given
        Product product = createProduct(name, 17000);
        when(purgomalumClient.containsProfanity(product.getName())).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변경될 상품 가격 음수거나 null인 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-10, -100})
    public void changePriceWithNegativePrice(int price) {
        // given
        Product product = createProduct("강정치킨", price);

        // when, then
        assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품이 속한 각 메뉴의 가격이 메뉴상품들의 가격의 합보다 크면 숨김 처리한다")
    @ParameterizedTest
    @CsvSource({"16000,33000","12000,48000"})
    public void changePriceNotDisplay(int menuPrice, int menuProductPrice) {
        // given
        Product product = createProduct("후라이드", menuProductPrice);
        Menu menu = createMenu("후라이드+후라이드", menuPrice, Arrays.asList(
                createMenuProduct(product, 1),
                createMenuProduct(product, 1)));
        when(productRepository.findById(any())).thenReturn(Optional.of(createProduct("후라이드", 15000)));

        // when
        Product expectedProduct = productService.changePrice(UUID.randomUUID(), product);

        // then
        assertAll(
                () -> assertThat(menu.isDisplayed()).isFalse());
    }
}
