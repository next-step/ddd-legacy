package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.MockPurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductTest {

    private ProductService productService;
    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();

    @Mock
    private final PurgomalumClient purgomalumClient = new MockPurgomalumClient();

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("단품 생성")
    @Test
    void create() {
        String productName = "강정치킨";
        BigDecimal productPrice = BigDecimal.valueOf(17000);

        Product actual = productRequest(productName, productPrice);
        Product expected = productService.create(actual);
        assertAll(
                () -> assertThat(expected.getName()).isEqualTo(expected.getName()),
                () -> assertThat(expected.getPrice()).isEqualTo(expected.getPrice())
        );
    }

    @DisplayName("단품 생성 - null 또는 음수값을 생성될 단품의 가격으로 정할 수 없다.")
    @ValueSource(longs = -1L)
    @NullSource
    @ParameterizedTest
    void createValidationPrice(Long price) {
        String productName = "강정치킨";

        BigDecimal productPrice = null;
        if (price != null) {
            productPrice = BigDecimal.valueOf(price);
        }

        Product actual = productRequest(productName, productPrice);
        assertThatThrownBy(() -> productService.create(actual))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("단품 생성 - null 또는 비속어를 생성될 단품의 이름으로 정할 수 없다.")
    @ValueSource(strings = "비속어")
    @NullSource
    @ParameterizedTest
    void createValidationName(String productName) {
        if (productName != null) {
            given(purgomalumClient.containsProfanity(productName)).willReturn(true);
        }
        BigDecimal productPrice = BigDecimal.valueOf(17000);

        Product actual = productRequest(productName, productPrice);
        assertThatThrownBy(() -> productService.create(actual))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("단품 가격 변경")
    @Test
    void changePrice() {
        String productName = "강정치킨";
        BigDecimal originalPrice = BigDecimal.valueOf(17000);
        BigDecimal changePrice = BigDecimal.valueOf(16000);
        Product actual = productRequest(productName, originalPrice);
        productRepository.save(actual);

        Product expected = productService.changePrice(actual.getId(), new Product(changePrice));
        assertThat(expected.getPrice()).isEqualTo(changePrice);
    }

    @DisplayName("단품 가격 변경 - 음수값으로 가격을 변경할 수 없다.")
    @ValueSource(longs = -1L)
    @ParameterizedTest
    void changePriceValidationNegativePrice(Long price) {
        Product actual = savedProduct();
        BigDecimal changePrice = BigDecimal.valueOf(price);

        assertThatThrownBy(() -> productService.changePrice(actual.getId(), new Product(changePrice)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("단품 가격 변경 - null 로 가격을 변경할 수 없다.")
    @NullSource
    @ParameterizedTest
    void changePriceValidationNullPrice(BigDecimal changePrice) {
        Product actual = savedProduct();

        assertThatThrownBy(() -> productService.changePrice(actual.getId(), new Product(changePrice)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("단품 전체 조회")
    @Test
    void findAll() {
        savedProduct();
        savedProduct();
        assertThat(productService.findAll().size())
                .isEqualTo(2);
    }

    private Product productRequest(String name, BigDecimal price) {
        return new Product(name, price);
    }

    private Product savedProduct() {
        String productName = "강정치킨";
        BigDecimal originalPrice = BigDecimal.valueOf(17000);
        return productRepository.save(productRequest(productName, originalPrice));
    }

}
