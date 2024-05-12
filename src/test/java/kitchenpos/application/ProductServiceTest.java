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
import org.junit.jupiter.api.Nested;
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
import static kitchenpos.fixture.ProductFixture.productChangePriceRequest;
import static kitchenpos.fixture.ProductFixture.productCreateRequest;
import static kitchenpos.fixture.ProductFixture.productResponse;
import static kitchenpos.fixture.ProductFixture.가격_18000;
import static kitchenpos.fixture.ProductFixture.가격_20000;
import static kitchenpos.fixture.ProductFixture.이름_양념치킨;
import static kitchenpos.fixture.ProductFixture.이름_후라이드치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("상품 서비스 테스트")
@ApplicationMockTest
class ProductServiceTest {
    private UUID ID_양념치킨;
    private Product 상품_양념치킨;
    private Product 상품_후라이드치킨;
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
        상품_양념치킨 = productResponse(이름_양념치킨, 가격_20000);
        상품_후라이드치킨 = productResponse(이름_후라이드치킨, 가격_18000);
        ID_양념치킨 = 상품_양념치킨.getId();
    }

    @DisplayName("상품 등록")
    @Nested
    class ProductCreate {
        @DisplayName("[성공]")
        @Nested
        class Success {
            @DisplayName("상품을 등록한다.")
            @Test
            void creatProduct() {
                // given
                Product request = productCreateRequest(이름_양념치킨, 가격_20000);
                when(productRepository.save(any())).thenReturn(상품_양념치킨);

                // when
                Product result = productService.create(request);

                // then
                assertAll(
                        () -> assertThat(result.getId()).isNotNull(),
                        () -> assertThat(result.getName()).isEqualTo(이름_양념치킨),
                        () -> assertThat(result.getPrice()).isEqualTo(가격_20000)
                );
            }
        }

        @DisplayName("[실패]")
        @Nested
        class Fail {
            @DisplayName("이름은 필수로 입력해야 한다.")
            @NullSource
            @ParameterizedTest
            void name1(String name) {
                // given
                Product request = productCreateRequest(name, 가격_20000);

                // when
                // then
                assertThatThrownBy(() -> productService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("이름에 비속어를 포함하지 않는다.")
            @ValueSource(strings = {"욕설", "비속어", "나쁜말"})
            @ParameterizedTest
            void name2(String name) {
                // given
                Product request = productCreateRequest(name, 가격_20000);

                // when
                when(purgomalumClient.containsProfanity(name)).thenReturn(true);

                // then
                assertThatThrownBy(() -> productService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("가격은 필수로 입력해야 한다.")
            @NullSource
            @ParameterizedTest
            void price1(BigDecimal price) {
                // given
                Product request = productCreateRequest(이름_양념치킨, price);

                // when
                // then
                assertThatThrownBy(() -> productService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("가격은 0원 이상이다.")
            @ValueSource(longs = {-1, -1000, -9999999})
            @ParameterizedTest
            void price2(long price) {
                // given
                Product request = productCreateRequest(이름_양념치킨, BigDecimal.valueOf(price));

                // when
                // then
                assertThatThrownBy(() -> productService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @DisplayName("상품 가격 수정")
    @Nested
    class ProductPriceChange {
        @DisplayName("[성공]")
        @Nested
        class Success {
            @DisplayName("상품의 가격이 수정된다.")
            @Test
            void changeProductPriceTest() {
                // given
                Product request = productChangePriceRequest(가격_18000);
                when(productRepository.findById(any())).thenReturn(Optional.of(상품_양념치킨));

                // when
                Product result = productService.changePrice(ID_양념치킨, request);

                // then
                assertThat(result.getPrice()).isEqualTo(request.getPrice());
            }
        }

        @DisplayName("[실패]")
        @Nested
        class Fail {
            @DisplayName("가격은 필수로 입력해야 한다.")
            @NullSource
            @ParameterizedTest
            void price1(BigDecimal price) {
                // given
                Product request = productChangePriceRequest(price);

                // when
                // then
                assertThatThrownBy(() -> productService.changePrice(ID_양념치킨, request))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("가격은 0원 이상이다.")
            @ValueSource(longs = {-1, -1000, -9999999})
            @ParameterizedTest
            void price2(long price) {
                // given
                Product request = productChangePriceRequest(BigDecimal.valueOf(price));

                // when
                // then
                assertThatThrownBy(() -> productService.changePrice(ID_양념치킨, request))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("상품을 포함한 각 메뉴의 가격이 상품의 (가격 * 수량)의 총 합보다 비싸면 메뉴는 노출하지 않는다.")
            @Test
            void pricePolicy() {
                // given
                BigDecimal 변경할_상품가격 = BigDecimal.valueOf(10_000);
                List<Menu> 강정치킨상품_포함된_메뉴들 = settingMenus();

                when(productRepository.findById(any())).thenReturn(Optional.of(상품_양념치킨));
                when(menuRepository.findAllByProductId(any())).thenReturn(강정치킨상품_포함된_메뉴들);

                Product request = productChangePriceRequest(변경할_상품가격);

                // when
                productService.changePrice(ID_양념치킨, request);

                // then
                강정치킨상품_포함된_메뉴들.forEach(menu -> assertThat(menu.isDisplayed()).isFalse());
            }
        }
    }

    @DisplayName("상품 목록을 볼 수 있다")
    @Test
    void getMenuGroups() {
        // given
        List<Product> list = List.of(상품_양념치킨, 상품_후라이드치킨);
        when(productRepository.findAll()).thenReturn(list);

        // when
        List<Product> result = productService.findAll();

        // then
        assertThat(result).containsExactly(상품_양념치킨, 상품_후라이드치킨);
    }

    private List<Menu> settingMenus() {
        // 메뉴구성상품 종류 (상품, 상품수량)
        MenuProduct 강정치킨_1개 = menuProductResponse(상품_양념치킨, 1);
        MenuProduct 강정치킨_2개 = menuProductResponse(상품_양념치킨, 2);
        MenuProduct 후라이드치킨_1개 = menuProductResponse(상품_후라이드치킨, 1);

        // 메뉴List (가격, 매뉴구성상품......)
        Menu MENU_두마리치킨 = menuPriceAndMenuProductResponse(BigDecimal.valueOf(30_000), 강정치킨_1개, 후라이드치킨_1개);
        Menu MENU_추천메뉴 = menuPriceAndMenuProductResponse(BigDecimal.valueOf(40_000), 강정치킨_2개, 후라이드치킨_1개);
        return List.of(MENU_두마리치킨, MENU_추천메뉴);
    }
}
