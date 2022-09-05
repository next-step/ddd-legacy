package kitchenpos.integration;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Product;
import kitchenpos.integration.mock.MemoryMenuRepository;
import kitchenpos.integration.mock.MemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static kitchenpos.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@Import(TestConfig.class)
class ProductServiceTest {

    @Autowired
    MemoryProductRepository productRepository;

    @Autowired
    MemoryMenuRepository menuRepository;

    @Autowired
    ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository.clear();
        menuRepository.clear();
    }

    @DisplayName("상품의 가격 0원보다 작을 수 없다.")
    @Test
    void create_IllegalPrice() {
        // given
        Product request = aChickenProduct(-10_000);

        // when + then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "상품의 이름은 빈 값을 허용하지 않는다. source = {0}")
    @NullSource
    void create_EmptyName(String source) {
        // given
        Product request = aProduct(source, 10_000);

        // when + then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "상품의 이름은 비속어를 허용하지 않는다. source = {0}")
    @ValueSource(strings = {"바보"})
    void create_ProfaneName(String source) {
        // given
        Product request = aProduct(source, 10_000);

        // when + then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 생성")
    @Test
    void create() {
        // given
        Product request = aChickenProduct(10_000);

        // when
        Product saved = productService.create(request);

        // then
        assertThat(saved.getId()).isNotNull();
    }

    @ParameterizedTest(name = "상품의 가격을 변경하면 상품을 포함하는 메뉴들은 가격에 따라 숨겨질 수 있다. price = {0}")
    @MethodSource("provideArgumentsForChangePrice")
    void changePrice(int originalPrice, boolean expected) {
        // given
        Product product = productService.create(aChickenProduct(10_000));

        Menu menu = aMenu("후라이드 치킨", originalPrice, aMenuProduct(product, 1));
        menu.setId(UUID.randomUUID());
        menuRepository.save(menu);

        // when
        Product changePriceRequest = new Product();
        changePriceRequest.setPrice(BigDecimal.valueOf(9_500));
        productService.changePrice(product.getId(), changePriceRequest);

        // then
        assertThat(menu.isDisplayed()).isEqualTo(expected);
    }

    public static Stream<Arguments> provideArgumentsForChangePrice() {
        return Stream.of(
                Arguments.of(9_400, true),
                Arguments.of(9_500, true),
                Arguments.of(9_600, false)
        );
    }
}
