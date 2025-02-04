package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static kitchenpos.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("상품 서비스 단위 테스트")
class ProductServiceUnitTest {

    private ProductRepository productRepository = mock(ProductRepository.class);
    private MenuRepository menuRepository = mock(MenuRepository.class);
    private PurgomalumClient purgomalumClient = mock(PurgomalumClient.class);
    private ProductService productService = new ProductService(productRepository, menuRepository, purgomalumClient);

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        menuRepository = mock(MenuRepository.class);
        purgomalumClient = mock(PurgomalumClient.class);
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품을 등록할 수 있습니다.")
    @Test
    void create() {
        when(purgomalumClient.containsProfanity(FRIED_CHICKEN)).thenReturn(false);
        when(productRepository.save(any(Product.class))).then(returnsFirstArg());

        final Product product = product(null, FRIED_CHICKEN, FRIED_CHICKEN_PRICE);
        final Product actual = productService.create(product);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(FRIED_CHICKEN),
                () -> assertThat(actual.getPrice()).isEqualByComparingTo(FRIED_CHICKEN_PRICE)
        );
    }

    @DisplayName("상품명은 1자 이상이어야 합니다.")
    @ParameterizedTest(name = "상품명 : `{0}`")
    @NullAndEmptySource
    void createWithEmptyName(final String name) {
        final Product product = product(null, name, FRIED_CHICKEN_PRICE);
        when(purgomalumClient.containsProfanity(name)).thenReturn(false);
        when(productRepository.save(any(Product.class))).then(returnsFirstArg());

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품명에는 비속어나 욕설을 사용할 수 없습니다.")
    @ParameterizedTest(name = "상품명 : `{0}`")
    @ValueSource(strings = {"비속어", "욕설", "그XX"})
    void createWithProfanity(final String name) {
        final Product product = product(null, name, FRIED_CHICKEN_PRICE);
        when(purgomalumClient.containsProfanity(name)).thenReturn(true);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격은 0원 이상이어야 합니다.")
    @ParameterizedTest(name = "상품 가격 : `{0}`")
    @ValueSource(strings = {"-1", "-1000", "-10000"})
    void createWithNegativePrice(final String price) {
        final Product product = product(null, FRIED_CHICKEN, new BigDecimal(price));
        when(purgomalumClient.containsProfanity(FRIED_CHICKEN)).thenReturn(false);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격이 비어있으면 예외가 발생합니다.")
    @ParameterizedTest(name = "상품 가격 : `{0}`")
    @NullSource
    void createWithEmptyPrice(final BigDecimal nullPrice) {
        final Product product = product(null, FRIED_CHICKEN, nullPrice);
        when(purgomalumClient.containsProfanity(FRIED_CHICKEN)).thenReturn(false);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격을 변경할 수 있습니다.")
    @ParameterizedTest(name = "상품 가격 : `{0}`")
    @ValueSource(strings = {"16000", "16010", "16020"})
    void changePrice(final String price) {
        final BigDecimal changedPrice = new BigDecimal(price);
        final UUID productId = createProductId();
        final Product product = product(productId, FRIED_CHICKEN, FRIED_CHICKEN_PRICE);
        final Product changedPriceProduct = product(product.getId(), FRIED_CHICKEN, changedPrice);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(menuRepository.findAllByProductId(product.getId())).thenReturn(emptyList());
        when(productRepository.save(any(Product.class))).then(returnsFirstArg());

        final Product actual = productService.changePrice(productId, changedPriceProduct);
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getPrice()).isEqualByComparingTo(changedPrice)
        );
    }

    @DisplayName("변경할 상품 가격이 0원 이상이어야 합니다.")
    @ParameterizedTest(name = "입력값 `{0}`")
    @ValueSource(strings = {"-1", "-1000", "-10000"})
    void changePriceWithNegativePrice(final String price) {
        final UUID productId = createProductId();
        final BigDecimal negativePrice = new BigDecimal(price);

        assertThatThrownBy(() ->
                productService.changePrice(productId, product(productId, FRIED_CHICKEN, negativePrice))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변경할 상품 가격이 비어있으면 예외가 발생합니다.")
    @ParameterizedTest(name = "상품 가격 : `{0}`")
    @NullSource
    void changePriceWithEmptyPrice(final BigDecimal price) {
        final UUID productId = createProductId();

        assertThatThrownBy(() ->
                productService.changePrice(productId, product(productId, FRIED_CHICKEN, price))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품이 존재하지 않으면 상품 가격을 변경할 수 없습니다.")
    @ParameterizedTest(name = "상품 가격 : `{0}`")
    @ValueSource(strings = {"16000", "16010", "16020"})
    void changePriceWithNonExistentProduct(final String price) {
        final UUID productId = createProductId();
        final BigDecimal changedPrice = new BigDecimal(price);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                productService.changePrice(productId, product(productId, FRIED_CHICKEN, changedPrice))
        ).isInstanceOf(NoSuchElementException.class);
    }
}
