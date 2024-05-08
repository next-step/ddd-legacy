package kitchenpos.application;

import kitchenpos.ApplicationMockTest;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Optional;

import static kitchenpos.fixture.ProductFixture.NAME_강정치킨;
import static kitchenpos.fixture.ProductFixture.PRICE_17000;
import static kitchenpos.fixture.ProductFixture.PRICE_18000;
import static kitchenpos.fixture.ProductFixture.productChangePriceRequest;
import static kitchenpos.fixture.ProductFixture.productCreateRequest;
import static kitchenpos.fixture.ProductFixture.productResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("상품 서비스 테스트")
@ApplicationMockTest
class ProductServiceTest {
    private static Product PRODUCT_강정치킨;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        PRODUCT_강정치킨 = productResponse(NAME_강정치킨, PRICE_17000);
    }

    @DisplayName("상품을 등록한다.")
    @Test
    void creatProduct() {
        // given
        Product request = productCreateRequest(NAME_강정치킨, PRICE_17000);
        when(productRepository.save(any())).thenReturn(PRODUCT_강정치킨);

        // when
        Product result = productService.create(request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo(NAME_강정치킨),
                () -> assertThat(result.getPrice()).isEqualTo(PRICE_17000)
        );
    }

    @DisplayName("상품을 등록할 때, 이름은 공백일 수 없다.")
    @NullSource
    @ParameterizedTest
    void createProduct_nullNameException(String name) {
        // given
        Product request = productCreateRequest(name, PRICE_17000);

        // when
        // then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품을 등록할 때, 이름에 비속어가 포함되면 예외가 발생한다.")
    @ValueSource(strings = {"욕설", "비속어", "나쁜말"})
    @ParameterizedTest
    void createProduct_containProfanityNameException(String name) {
        // given
        Product request = productCreateRequest(name, PRICE_17000);

        // when
        when(purgomalumClient.containsProfanity(name)).thenReturn(true);

        // then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품을 등록할때, 가격이 공백면 예외가 발생한다.")
    @NullSource
    @ParameterizedTest
    void createProduct_nullPriceException(BigDecimal price) {
        // given
        Product request = productCreateRequest(NAME_강정치킨, price);

        // when
        // then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품을 등록할때, 가격이 0원보다 작으면 예외가 발생한다.")
    @ValueSource(longs = {-1, -1000, -9999999})
    @ParameterizedTest
    void createProduct_lessThenZeroPriceException(long price) {
        // given
        Product request = productCreateRequest(NAME_강정치킨, BigDecimal.valueOf(price));

        // when
        // then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격이 수정된다.")
    @Test
    void changeProductPriceTest() {
        // given
        Product request = productChangePriceRequest(PRICE_18000);
        when(productRepository.findById(any())).thenReturn(Optional.of(PRODUCT_강정치킨));

        // when
        Product result = productService.changePrice(PRODUCT_강정치킨.getId(), request);

        // then
        assertThat(result.getPrice()).isEqualTo(request.getPrice());
    }
}
