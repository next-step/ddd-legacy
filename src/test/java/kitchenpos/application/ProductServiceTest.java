package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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

    private static final UUID ID = UUID.randomUUID();
    private static final String NAME = "NEW PRODUCT";
    private static final BigDecimal PRICE = new BigDecimal("10000");

    private Product expectedProduct;

    @BeforeEach
    void setUp() {
        expectedProduct = new Product();
        expectedProduct.setId(ID);
        expectedProduct.setName(NAME);
        expectedProduct.setPrice(PRICE);
    }


    @DisplayName("새로운 상품을 추가할 수 있다.")
    @Test
    void create() {
        // given
        given(productRepository.save(any(Product.class)))
                .willReturn(expectedProduct);

        // when
        final Product actual = productService.create(expectedProduct);

        // then
        assertAll(
                () -> assertThat(actual.getName())
                        .isEqualTo(NAME),
                () -> assertThat(actual.getPrice())
                        .isEqualTo(PRICE)
        );
    }

    @DisplayName("상품의 가격은 비어있을 수 없다.")
    @Test
    void createEmptyPrice() {
        // given
        expectedProduct.setPrice(null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(expectedProduct));
    }

    @DisplayName("상품의 가격은 음수일 수 없다.")
    @Test
    void createNegativePrice() {
        // given
        expectedProduct.setPrice(new BigDecimal("-1"));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(expectedProduct));
    }

    @DisplayName("상품의 이름은 비어있을 수 없다.")
    @Test
    void createEmptyName() {
        // given
        expectedProduct.setName(null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(expectedProduct));
    }

    @DisplayName("상품의 이름은 저속해서는 안된다.")
    @Test
    void createProfanityName() {
        // given
        given(purgomalumClient.containsProfanity(any(String.class)))
                .willReturn(true);


        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.create(expectedProduct));
    }

    @DisplayName("상품의 가격을 수정할 수 있다.")
    @Test
    void changePrice() {
        // given
        final BigDecimal CHANGED_PRICE = new BigDecimal("20000");
        final UUID productId = expectedProduct.getId();
        expectedProduct.setPrice(CHANGED_PRICE);
        given(productRepository.findById(any(UUID.class)))
                .willReturn(Optional.of(expectedProduct));
        given(menuRepository.findAllByProductId(any(UUID.class)))
                .willReturn(Collections.emptyList());

        // when
        final Product actual = productService.changePrice(productId, expectedProduct);

        // then
        assertThat(actual.getPrice())
                .isEqualTo(CHANGED_PRICE);
    }

    @DisplayName("상품의 가격을 빈값으로 수정할 수 없다.")
    @Test
    void changeEmptyPrice() {
        // given
        final UUID productId = expectedProduct.getId();
        expectedProduct.setPrice(null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.changePrice(productId, expectedProduct));
    }

    @DisplayName("상품의 가격을 음수로 수정할 수 없다.")
    @Test
    void changeNegativePrice() {
        // given
        final UUID productId = expectedProduct.getId();
        expectedProduct.setPrice(new BigDecimal("-1"));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.changePrice(productId, expectedProduct));
    }

    @DisplayName("존재하지 않는 상품은 수정할 수 없다.")
    @Test
    void changeNonExistProduct() {
        // given
        final BigDecimal CHANGED_PRICE = new BigDecimal("20000");
        final UUID productId = expectedProduct.getId();
        expectedProduct.setPrice(CHANGED_PRICE);
        given(productRepository.findById(any(UUID.class)))
                .willReturn(Optional.empty());

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> productService.changePrice(productId, expectedProduct));
    }
}
