package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.AdditionalAnswers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static kitchenpos.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Autowired
    @MockBean
    private ProductRepository productRepository;

    @Autowired
    @MockBean
    private MenuRepository menuRepository;

    @Autowired
    @MockBean
    private PurgomalumClient purgomalumClient;

    @Autowired
    private ProductService productService;

    @DisplayName("상품을 등록할 수 있습니다.")
    @Test
    void create() {
        final Product product = product(null, DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE);
        when(purgomalumClient.containsProfanity(DEFAULT_PRODUCT_NAME)).thenReturn(false);
        when(productRepository.save(any(Product.class))).then(returnsFirstArg());

        final Product actual = productService.create(product);

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(DEFAULT_PRODUCT_NAME),
                () -> assertThat(actual.getPrice()).isEqualByComparingTo(DEFAULT_PRODUCT_PRICE)
        );
    }

    @DisplayName("상품명은 1자 이상이어야 합니다.")
    @ParameterizedTest(name = "입력값 `{0}`")
    @NullAndEmptySource
    void createWithEmptyName(final String name) {
        final Product product = product(null, name, DEFAULT_PRODUCT_PRICE);
        when(purgomalumClient.containsProfanity(name)).thenReturn(false);
        when(productRepository.save(any(Product.class))).then(returnsFirstArg());

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품명에는 비속어나 욕설을 사용할 수 없습니다.")
    @ParameterizedTest(name = "입력값 `{0}`")
    @ValueSource(strings = {"비속어", "욕설", "그XX"})
    void createWithProfanity(final String name) {
        final Product product = product(null, name, DEFAULT_PRODUCT_PRICE);
        when(purgomalumClient.containsProfanity(name)).thenReturn(true);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격은 0원 이상이어야 합니다.")
    @ParameterizedTest(name = "입력값 `{0}`")
    @ValueSource(strings = {"-1", "-1000", "-10000"})
    void createWithNegativePrice(final String price) {
        final Product product = product(null, DEFAULT_PRODUCT_NAME, new BigDecimal(price));
        when(purgomalumClient.containsProfanity(DEFAULT_PRODUCT_NAME)).thenReturn(false);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격이 비어있으면 예외가 발생합니다.")
    @Test
    void createWithEmptyPrice() {
        final Product product = product(null, DEFAULT_PRODUCT_NAME, null);
        when(purgomalumClient.containsProfanity(DEFAULT_PRODUCT_NAME)).thenReturn(false);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격을 변경할 수 있습니다.")
    @Test
    void changePrice() {
        final UUID productId = createProductId();
        final Product product = product(productId, DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE);
        final BigDecimal changedPrice = BigDecimal.valueOf(16010);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(menuRepository.findAllByProductId(productId)).thenReturn(emptyList());
        when(productRepository.save(any(Product.class))).then(returnsFirstArg());
        final Product actual = productService.changePrice(productId, product(productId, DEFAULT_PRODUCT_NAME, changedPrice));

        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isEqualTo(productId),
                () -> assertThat(actual.getName()).isEqualTo(DEFAULT_PRODUCT_NAME),
                () -> assertThat(actual.getPrice()).isEqualByComparingTo(changedPrice)
        );
    }

    @DisplayName("변경할 상품 가격이 0원 이상이어야 합니다.")
    @ParameterizedTest(name = "입력값 `{0}`")
    @ValueSource(strings = {"-1", "-1000", "-10000"})
    void changePriceWithNegativePrice(final String price) {
        final UUID productId = createProductId();
        final Product product = product(productId, DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(menuRepository.findAllByProductId(productId)).thenReturn(emptyList());

        assertThatThrownBy(() ->
                productService.changePrice(productId, product(productId, DEFAULT_PRODUCT_NAME, new BigDecimal(price))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변경할 상품 가격이 비어있으면 예외가 발생합니다.")
    @Test
    void changePriceWithEmptyPrice() {
        final UUID productId = createProductId();
        final Product product = product(productId, DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(menuRepository.findAllByProductId(productId)).thenReturn(emptyList());

        assertThatThrownBy(() ->
                productService.changePrice(productId, product(productId, DEFAULT_PRODUCT_NAME, null)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
