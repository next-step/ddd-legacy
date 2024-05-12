package kitchenpos.application;

import kitchenpos.ApplicationMockTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.menuPriceAndMenuProductResponse;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.ProductFixture.NAME_양념치킨;
import static kitchenpos.fixture.ProductFixture.NAME_후라이드치킨;
import static kitchenpos.fixture.ProductFixture.PRICE_18000;
import static kitchenpos.fixture.ProductFixture.PRICE_20000;
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
    private UUID ID_양념치킨;
    private Product PRODUCT_양념치킨;
    private Product PRODUCT_후라이드치킨;
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
        PRODUCT_양념치킨 = productResponse(NAME_양념치킨, PRICE_20000);
        PRODUCT_후라이드치킨 = productResponse(NAME_후라이드치킨, PRICE_18000);
        ID_양념치킨 = PRODUCT_양념치킨.getId();
    }

    @DisplayName("상품을 등록한다.")
    @Test
    void creatProduct() {
        // given
        Product request = productCreateRequest(NAME_양념치킨, PRICE_20000);
        when(productRepository.save(any())).thenReturn(PRODUCT_양념치킨);

        // when
        Product result = productService.create(request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo(NAME_양념치킨),
                () -> assertThat(result.getPrice()).isEqualTo(PRICE_20000)
        );
    }

    @DisplayName("상품을 등록할 때, 이름은 공백일 수 없다.")
    @NullSource
    @ParameterizedTest
    void createProduct_nullNameException(String name) {
        // given
        Product request = productCreateRequest(name, PRICE_20000);

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
        Product request = productCreateRequest(name, PRICE_20000);

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
        Product request = productCreateRequest(NAME_양념치킨, price);

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
        Product request = productCreateRequest(NAME_양념치킨, BigDecimal.valueOf(price));

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
        when(productRepository.findById(any())).thenReturn(Optional.of(PRODUCT_양념치킨));

        // when
        Product result = productService.changePrice(ID_양념치킨, request);

        // then
        assertThat(result.getPrice()).isEqualTo(request.getPrice());
    }

    @DisplayName("상품의 가격을 수정할 때, 가격이 공백이면 예외가 발생한다.")
    @NullSource
    @ParameterizedTest
    void changePrice_nullPriceException(BigDecimal price) {
        // given
        Product request = productChangePriceRequest(price);

        // when
        // then
        assertThatThrownBy(() -> productService.changePrice(ID_양념치킨, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 수정할 때, 가격이 0원보다 작으면 예외가 발생한다.")
    @ValueSource(longs = {-1, -1000, -9999999})
    @ParameterizedTest
    void changePrice_lessThenZeroPriceException(long price) {
        // given
        Product request = productChangePriceRequest(BigDecimal.valueOf(price));

        // when
        // then
        assertThatThrownBy(() -> productService.changePrice(ID_양념치킨, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 수정할 때, 상품을 포함한 각 메뉴의 가격이 상품의 (가격 * 수량)의 총 합보다 비싸면 메뉴는 노출하지 않는다.")
    @Test
    void changePrice_menuPriceCheck_menuNotDisplayed() {
        // given
        BigDecimal 변경할_상품가격 = BigDecimal.valueOf(10_000);
        List<Menu> 강정치킨상품_포함된_메뉴들 = settingMenus();

        when(productRepository.findById(any())).thenReturn(Optional.of(PRODUCT_양념치킨));
        when(menuRepository.findAllByProductId(any())).thenReturn(강정치킨상품_포함된_메뉴들);

        Product request = productChangePriceRequest(변경할_상품가격);

        // when
        productService.changePrice(ID_양념치킨, request);

        // then
        강정치킨상품_포함된_메뉴들.forEach(menu -> assertThat(menu.isDisplayed()).isFalse());
    }

    @DisplayName("상품 목록을 볼 수 있다")
    @Test
    void getMenuGroups() {
        // given
        List<Product> list = List.of(PRODUCT_양념치킨, PRODUCT_후라이드치킨);
        when(productRepository.findAll()).thenReturn(list);

        // when
        List<Product> result = productService.findAll();

        // then
        assertThat(result).containsExactly(PRODUCT_양념치킨, PRODUCT_후라이드치킨);
    }

    private List<Menu> settingMenus() {
        // 메뉴구성상품 종류 (상품, 상품수량)
        MenuProduct 강정치킨_1개 = menuProductResponse(PRODUCT_양념치킨, 1);
        MenuProduct 강정치킨_2개 = menuProductResponse(PRODUCT_양념치킨, 2);
        MenuProduct 후라이드치킨_1개 = menuProductResponse(PRODUCT_후라이드치킨, 1);

        // 메뉴List (가격, 매뉴구성상품......)
        Menu MENU_두마리치킨 = menuPriceAndMenuProductResponse(BigDecimal.valueOf(30_000), 강정치킨_1개, 후라이드치킨_1개);
        Menu MENU_추천메뉴 = menuPriceAndMenuProductResponse(BigDecimal.valueOf(40_000), 강정치킨_2개, 후라이드치킨_1개);
        return List.of(MENU_두마리치킨, MENU_추천메뉴);
    }
}
