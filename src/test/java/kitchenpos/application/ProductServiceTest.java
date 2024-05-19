package kitchenpos.application;

import kitchenpos.domain.InMemoryMenuRepository;
import kitchenpos.domain.InMemoryProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.menuPriceAndMenuProductResponse;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.ProductFixture.productChangePriceRequest;
import static kitchenpos.fixture.ProductFixture.productCreateRequest;
import static kitchenpos.fixture.ProductFixture.productResponse;
import static kitchenpos.fixture.ProductFixture.가격_18000;
import static kitchenpos.fixture.ProductFixture.가격_20000;
import static kitchenpos.fixture.ProductFixture.이름_양념치킨;
import static kitchenpos.fixture.ProductFixture.이름_후라이드치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("상품 서비스 테스트")
class ProductServiceTest {
    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    private ProductService productService;

    private Product 상품_양념치킨;
    private Product 상품_후라이드치킨;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
        상품_양념치킨 = productResponse(이름_양념치킨, 가격_20000);
        상품_후라이드치킨 = productResponse(이름_후라이드치킨, 가격_18000);
    }

    @DisplayName("상품을 등록한다.")
    @Nested
    class ProductCreate {
        @DisplayName("[성공] 등록")
        @Test
        void success() {
            // given
            Product request = productCreateRequest(이름_양념치킨, 가격_20000);

            // when
            Product result = productService.create(request);

            // then
            assertAll(
                    () -> assertThat(result.getId()).isNotNull(),
                    () -> assertThat(result.getName()).isEqualTo(이름_양념치킨),
                    () -> assertThat(result.getPrice()).isEqualTo(가격_20000)
            );
        }

        @DisplayName("[실패] 이름은 필수로 입력해야 한다.")
        @NullSource
        @ParameterizedTest
        void fail1(String name) {
            // given
            Product request = productCreateRequest(name, 가격_20000);

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(request));
        }

        @DisplayName("[실패] 이름에 비속어를 포함하지 않는다.")
        @ValueSource(strings = {"욕설", "비속어", "나쁜말"})
        @ParameterizedTest
        void fail2(String name) {
            // given
            Product request = productCreateRequest(name, 가격_20000);

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(request));
        }

        @DisplayName("[실패] 가격은 필수로 입력해야 한다.")
        @NullSource
        @ParameterizedTest
        void fail3(BigDecimal price) {
            // given
            Product request = productCreateRequest(이름_양념치킨, price);

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(request));
        }

        @DisplayName("[실패] 가격은 0원 이상이다.")
        @ValueSource(longs = {-1, -1000, -9999999})
        @ParameterizedTest
        void fail4(long price) {
            // given
            Product request = productCreateRequest(이름_양념치킨, BigDecimal.valueOf(price));

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.create(request));
        }
    }

    @DisplayName("상품의 가격이 수정된다.")
    @Nested
    class ProductPriceChange {
        @DisplayName("[성공] 가격 수정")
        @Test
        void success() {
            // given
            UUID ID_양념치킨 = createProductId(상품_양념치킨);
            Product request = productChangePriceRequest(가격_20000);

            // when
            Product result = productService.changePrice(ID_양념치킨, request);

            // then
            assertThat(result.getPrice()).isEqualTo(request.getPrice());
        }

        @DisplayName("[실패] 가격은 필수로 입력해야 한다.")
        @NullSource
        @ParameterizedTest
        void fail1(BigDecimal price) {
            // given
            UUID ID_양념치킨 = createProductId(상품_양념치킨);
            Product request = productChangePriceRequest(price);

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.changePrice(ID_양념치킨, request));
        }

        @DisplayName("[실패] 가격은 0원 이상이다.")
        @ValueSource(longs = {-1, -1000, -9999999})
        @ParameterizedTest
        void fail2(long price) {
            // given
            UUID ID_양념치킨 = createProductId(상품_양념치킨);
            Product request = productChangePriceRequest(BigDecimal.valueOf(price));

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> productService.changePrice(ID_양념치킨, request));
        }

        @DisplayName("[실패] 상품을 포함한 각 메뉴의 가격이 상품의 (가격 * 수량)의 총 합보다 비싸면 메뉴는 노출하지 않는다.")
        @Test
        void fail3() {
            // given
            UUID ID_양념치킨 = createProductId(상품_양념치킨);
            BigDecimal 변경할_상품가격 = BigDecimal.valueOf(10_000);
            List<Menu> 강정치킨상품_포함된_메뉴들 = settingMenus();
            Product request = productChangePriceRequest(변경할_상품가격);

            // when
            productService.changePrice(ID_양념치킨, request);

            // then
            강정치킨상품_포함된_메뉴들.forEach(menu -> assertThat(menu.isDisplayed()).isFalse());
        }
    }

    @DisplayName("[성공] 상품 목록을 볼 수 있다")
    @Test
    void getMenuGroups() {
        // given
        createProducts(상품_양념치킨, 상품_후라이드치킨);

        // when
        List<Product> result = productService.findAll();

        // then
        assertThat(result).containsExactly(상품_양념치킨, 상품_후라이드치킨);
    }

    private void createProduct(Product product) {
        productRepository.save(product);
    }


    private void createProducts(Product... products) {
        Arrays.stream(products).forEach(this::createProduct);
    }

    private UUID createProductId(Product product) {
        return productRepository.save(product).getId();
    }

    private List<Menu> settingMenus() {
        createProducts(상품_양념치킨, 상품_후라이드치킨);

        // 메뉴구성상품 종류 (상품, 상품수량)
        MenuProduct 강정치킨_1개 = menuProductResponse(상품_양념치킨, 1);
        MenuProduct 강정치킨_2개 = menuProductResponse(상품_양념치킨, 2);
        MenuProduct 후라이드치킨_1개 = menuProductResponse(상품_후라이드치킨, 1);

        // 메뉴List (가격, 매뉴구성상품......)
        Menu MENU_두마리치킨 = createMenu(BigDecimal.valueOf(30_000), 강정치킨_1개, 후라이드치킨_1개);
        Menu MENU_추천메뉴 = createMenu(BigDecimal.valueOf(40_000), 강정치킨_2개, 후라이드치킨_1개);

        return List.of(MENU_두마리치킨, MENU_추천메뉴);
    }

    private Menu createMenu(BigDecimal price, MenuProduct... menuProducts) {
        Menu menu = menuPriceAndMenuProductResponse(price, menuProducts);
        return menuRepository.save(menu);
    }
}
