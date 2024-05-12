package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static kitchenpos.fixture.application.ProductFixture.changeProduct;
import static kitchenpos.fixture.application.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

    @ParameterizedTest
    @DisplayName("상품을 등록한다")
    @ValueSource(strings = {"", " ", "강정치킨"})
    void create(String name) {
        // given
        Product product = createProduct(name);
        given(productRepository.save(any())).willReturn(product);

        // when
        Product createdProduct = productService.create(product);

        // then
        assertThat(createdProduct.getId()).isEqualByComparingTo(product.getId());
    }

    @ParameterizedTest
    @DisplayName("상품의 이름은 비속어가 될 수 없다")
    @ValueSource(strings = {"비속어1", "비속어2"})
    void createProductNameCannotBeProfanity(String name) {
        Product product = createProduct(name);

        when(purgomalumClient.containsProfanity(name)).thenReturn(true);

        assertThatThrownBy(() -> {
            productService.create(product);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 가격은 0원 이상이어야 한다")
    void createProductPriceIsGreaterThanZero() {
        // given
        Product product = createProduct(BigDecimal.ZERO);
        given(productRepository.save(any())).willReturn(product);

        // when
        Product createdProduct = productService.create(product);

        // then
        assertThat(createdProduct.getId()).isEqualByComparingTo(product.getId());
    }

    @ParameterizedTest
    @DisplayName("상품의 이름은 null 이면 예외가 발생한다")
    @NullSource
    void createProductNameIsNotNull(String name) {
        Product product = createProduct(name);

        assertThatThrownBy(() -> {
            productService.create(product);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("상품의 가격은 null 이면 예외가 발생한다")
    @NullSource
    void createProductPriceIsNotNull(BigDecimal price) {
        Product product = createProduct(price);

        assertThatThrownBy(() -> {
            productService.create(product);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품의 가격을 수정한다")
    void changePrice() {
        Product product = createProduct("강정치킨", BigDecimal.valueOf(12000));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        Product request = changeProduct(BigDecimal.valueOf(10000));
        Product result = productService.changePrice(product.getId(), request);

        assertThat(result.getPrice()).isEqualTo(request.getPrice());
    }

    @ParameterizedTest
    @DisplayName("변경하려는 상품의 가격이 null 이면 예외가 발생한다")
    @NullSource
    void changePriceIsNull(BigDecimal price) {
        Product product = createProduct("강정치킨", BigDecimal.valueOf(12000));

        Product request = changeProduct(price);
        assertThatThrownBy(() -> {
            productService.changePrice(product.getId(), request);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("변경하려는 상품의 가격이 0보다 작으면 예외가 발생한다")
    @ValueSource(ints = {-1, -20000})
    void changePriceIsLowerThanZero(int price) {
        Product product = createProduct("강정치킨", BigDecimal.valueOf(12000));

        Product request = changeProduct(BigDecimal.valueOf(price));
        assertThatThrownBy(() -> {
            productService.changePrice(product.getId(), request);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
