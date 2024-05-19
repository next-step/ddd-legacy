package kitchenpos.application;

import kitchenpos.domain.InMemoryMenuGroupRepository;
import kitchenpos.domain.InMemoryMenuRepository;
import kitchenpos.domain.InMemoryProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
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
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static kitchenpos.fixture.MenuFixture.menuChangePriceRequest;
import static kitchenpos.fixture.MenuFixture.menuCreateRequest;
import static kitchenpos.fixture.MenuFixture.menuResponse;
import static kitchenpos.fixture.MenuFixture.가격_34000;
import static kitchenpos.fixture.MenuFixture.이름_순살치킨;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupResponse;
import static kitchenpos.fixture.MenuGroupFixture.이름_추천메뉴;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.ProductFixture.productResponse;
import static kitchenpos.fixture.ProductFixture.가격_18000;
import static kitchenpos.fixture.ProductFixture.가격_20000;
import static kitchenpos.fixture.ProductFixture.이름_양념치킨;
import static kitchenpos.fixture.ProductFixture.이름_후라이드치킨;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("메뉴 서비스 테스트")
class MenuServiceTest {
    private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final ProductRepository productRepository= new InMemoryProductRepository();
    private final PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    private MenuService menuService;

    private MenuGroup 메뉴그룹_추천메뉴;
    private Product 상품_양념치킨;
    private Product 상품_후라이드치킨;
    private MenuProduct 양념치킨_1개;
    private MenuProduct 후라이드치킨_1개;
    private UUID ID_추천메뉴;
    private UUID ID_순살치킨;
    private Menu 메뉴_순살치킨;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
        메뉴그룹_추천메뉴 = menuGroupResponse(이름_추천메뉴);
        ID_추천메뉴 = 메뉴그룹_추천메뉴.getId();
        상품_양념치킨 = productResponse(이름_양념치킨, 가격_20000);
        상품_후라이드치킨 = productResponse(이름_후라이드치킨, 가격_18000);
        양념치킨_1개 = menuProductResponse(상품_양념치킨, 1);
        후라이드치킨_1개 = menuProductResponse(상품_후라이드치킨, 1);
        메뉴_순살치킨 = menuResponse(이름_순살치킨, 가격_34000, ID_추천메뉴, true, 양념치킨_1개, 후라이드치킨_1개);
        ID_순살치킨 = 메뉴_순살치킨.getId();
    }

    @DisplayName("메뉴를 등록한다.")
    @Nested
    class MenuCreate {
        @DisplayName("[성공] 등록")
        @Test
        void success() {
            // given
            setUpBeforeCreateMenu();
            Menu request = menuCreateRequest(이름_순살치킨, 가격_34000, ID_추천메뉴, true, 양념치킨_1개, 후라이드치킨_1개);

            // when
            Menu result = menuService.create(request);

            // then
            assertAll(
                    () -> assertThat(result.getName()).isEqualTo(이름_순살치킨),
                    () -> assertThat(result.getPrice()).isEqualTo(가격_34000),
                    () -> assertThat(result.isDisplayed()).isTrue(),
                    () -> assertThat(result.getMenuGroup().getId()).isEqualTo(ID_추천메뉴),
                    () -> assertThat(result.getMenuProducts()).extracting("product", Product.class)
                            .contains(상품_양념치킨, 상품_후라이드치킨)
            );
        }

        @DisplayName("[실패] 이름은 필수로 입력해야 한다.")
        @NullSource
        @ParameterizedTest
        void fail1(String name) {
            // given
            setUpBeforeCreateMenu();
            Menu request = menuCreateRequest(name, 가격_34000, ID_추천메뉴, true, 양념치킨_1개, 후라이드치킨_1개);

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("[실패] 이름은 비속어를 포함하지 않는다.")
        @ValueSource(strings = {"욕설", "비속어"})
        @ParameterizedTest
        void fail2(String name) {
            // given
            setUpBeforeCreateMenu();
            Menu request = menuCreateRequest(name, 가격_34000, ID_추천메뉴, true, 양념치킨_1개, 후라이드치킨_1개);

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("[실패] 메뉴가격은 필수로 입력해야하고 `0`원 이상이다.")
        @NullSource
        @MethodSource("provideInvalidPrices")
        @ParameterizedTest
        void fail3(BigDecimal price) {
            // given
            setUpBeforeCreateMenu();
            Menu request = menuCreateRequest(이름_순살치킨, price, ID_추천메뉴, true, 양념치킨_1개, 후라이드치킨_1개);

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("[실패] {메뉴가격}이 {모든 메뉴구성상품의 (가격*수량) 총 합}보다 비싸지 않아야 한다.")
        @ValueSource(longs = {38_001, 50_000, 100_000})
        @ParameterizedTest
        void fail4(long price) {
            // given
            setUpBeforeCreateMenu();
            Menu request = menuCreateRequest(이름_순살치킨, BigDecimal.valueOf(price), ID_추천메뉴, true, 양념치킨_1개, 후라이드치킨_1개);

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("[실패] 메뉴는 이미 등록된 하나의 `메뉴 그룹`에 반드시 속한다.")
        @Test
        void fail5() {
            // given
            createProduct(상품_양념치킨);
            createProduct(상품_후라이드치킨);
            Menu request = menuCreateRequest(이름_순살치킨, 가격_34000, ID_추천메뉴, true, 양념치킨_1개, 후라이드치킨_1개);

            // when
            // then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[실패] 메뉴는 `메뉴구성 상품`을 `한 가지`이상 가진다.")
        @Test
        void fail6() {
            // given
            setUpBeforeCreateMenu();
            Menu request = menuCreateRequest(이름_순살치킨, 가격_34000, ID_추천메뉴, true);

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("[실패] `메뉴구성 상품`은 미리 등록된 `상품`을 가진다.")
        @Test
        void fail7() {
            // given
            createMenuGroup(메뉴그룹_추천메뉴);
            Menu request = menuCreateRequest(이름_순살치킨, 가격_34000, ID_추천메뉴, true, 양념치킨_1개);

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }

        @DisplayName("[실패] `메뉴구성 상품`은  수량이 0개 가진다.")
        @ValueSource(longs = {-1, -111, -999999})
        @ParameterizedTest
        void fail8(long quantity) {
            // given
            setUpBeforeCreateMenu();
            MenuProduct menuProduct = menuProductResponse(상품_양념치킨, quantity);
            Menu request = menuCreateRequest(이름_순살치킨, 가격_34000, ID_추천메뉴, true, menuProduct);

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.create(request));
        }

        private static Stream<BigDecimal> provideInvalidPrices() {
            return Stream.of(BigDecimal.valueOf(-1), BigDecimal.valueOf(-1000), BigDecimal.valueOf(-99999990));
        }
    }

    @DisplayName("메뉴의 가격을 수정한다.")
    @Nested
    class ChangePrice {
        @DisplayName("[성공] 가격 수정")
        @Test
        void success() {
            // given
            UUID ID_순살치킨 = createMenuId(메뉴_순살치킨);
            Menu request = menuChangePriceRequest(가격_34000);

            // when
            Menu result = menuService.changePrice(ID_순살치킨, request);

            // then
            assertAll(
                    () -> assertThat(result.getId()).isEqualTo(ID_순살치킨),
                    () -> assertThat(result.getPrice()).isEqualTo(가격_34000)
            );
        }

        @DisplayName("[실패] 해당 메뉴가 미리 등록되어 있는지 체크해야한다.")
        @Test
        void fail1() {
            // given
            Menu request = menuChangePriceRequest(가격_34000);

            // when
            // then
            assertThatThrownBy(() -> menuService.changePrice(ID_순살치킨, request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[실패] 가격은 필수로 입력해야하고 `0`원 이상이다.")
        @NullSource
        @MethodSource("provideInvalidPrices")
        @ParameterizedTest
        void fail2(BigDecimal price) {
            // given
            UUID ID_순살치킨 = createMenuId(메뉴_순살치킨);
            Menu request = menuChangePriceRequest(price);

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.changePrice(ID_순살치킨, request));
        }

        @DisplayName("[실패] {메뉴가격}이 {메뉴구성상품의 (가격*수량)의 총 합}보다 비싸지 않아야 한다.")
        @ValueSource(longs = {38_001, 50_000, 100_000})
        @ParameterizedTest
        void fail3(long price) {
            // given
            UUID ID_순살치킨 = createMenuId(메뉴_순살치킨);
            Menu request = menuChangePriceRequest(BigDecimal.valueOf(price));

            // when
            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.changePrice(ID_순살치킨, request));
        }

        private static Stream<BigDecimal> provideInvalidPrices() {
            return Stream.of(BigDecimal.valueOf(-1), BigDecimal.valueOf(-1000), BigDecimal.valueOf(-99999990));
        }
    }

    @DisplayName("메뉴가 노출된다")
    @Nested
    class DisplayChange {
        @DisplayName("[성공] 메뉴가 노출된다.")
        @Test
        void success1() {
            // given
            UUID ID_순살치킨 = createMenuId(메뉴_순살치킨);

            // when
            Menu result = menuService.display(ID_순살치킨);

            // then
            assertAll(
                    () -> assertThat(result.getId()).isEqualTo(ID_순살치킨),
                    () -> assertThat(result.isDisplayed()).isTrue()
            );
        }

        @DisplayName("[성공] 메뉴를 보이지 않게 할 수 있다.")
        @Test
        void success2() {
            // given
            UUID ID_순살치킨 = createMenuId(메뉴_순살치킨);

            // when
            Menu result = menuService.hide(ID_순살치킨);

            // then
            assertAll(
                    () -> assertThat(result.getId()).isEqualTo(ID_순살치킨),
                    () -> assertThat(result.isDisplayed()).isFalse()
            );
        }

        @DisplayName("[실패] 해당 메뉴가 미리 등록되어 있는지 체크해야한다.")
        @Test
        void fail1() {
            // given
            UUID menuId = ID_순살치킨;

            // when
            // then
            assertAll(
                    () -> assertThatThrownBy(() -> menuService.display(menuId))
                            .isInstanceOf(NoSuchElementException.class),
                    () -> assertThatThrownBy(() -> menuService.hide(menuId))
                            .isInstanceOf(NoSuchElementException.class)
            );
        }

        @DisplayName("[실패] {메뉴의 가격}이 {메뉴구성상품의 (가격*수량)의 총 합}보다 비싸지 않아야 한다.")
        @ValueSource(longs = {20_001, 30_000, 50_000})
        @ParameterizedTest
        void fail2(long price) {
            // given
            Menu menu = menuResponse(이름_순살치킨, BigDecimal.valueOf(price), ID_추천메뉴, true, 양념치킨_1개);
            UUID ID_순살치킨 = createMenuId(menu);

            // when
            // then
            assertThatIllegalStateException()
                    .isThrownBy(() -> menuService.display(ID_순살치킨));
        }
    }

    @DisplayName("[성공] 메뉴 목록을 볼 수 있다.")
    @Test
    void getMenus() {
        // given
        createMenuGroup(메뉴그룹_추천메뉴);
        createMenu(menuCreateRequest(이름_순살치킨, 가격_34000, ID_추천메뉴, true, 양념치킨_1개, 후라이드치킨_1개));

        // when
        List<Menu> result = menuService.findAll();

        // then
        assertAll(
                () -> assertThat(result.getFirst().getName()).isEqualTo(이름_순살치킨),
                () -> assertThat(result.getFirst().getPrice()).isEqualTo(가격_34000),
                () -> assertThat(result.getFirst().getMenuGroupId()).isEqualTo(ID_추천메뉴)
        );
    }

    private void createMenu(Menu menu) {
        menuRepository.save(menu);
    }

    private void createMenuGroup(MenuGroup menuGroup) {
        menuGroupRepository.save(menuGroup);
    }

    private void createProduct(Product product) {
        productRepository.save(product);
    }

    private void setUpBeforeCreateMenu() {
        createProduct(상품_양념치킨);
        createProduct(상품_후라이드치킨);
        createMenuGroup(메뉴그룹_추천메뉴);
    }

    private UUID createMenuId(Menu menu) {
        setUpBeforeCreateMenu();
        return menuRepository.save(menu).getId();
    }
}
