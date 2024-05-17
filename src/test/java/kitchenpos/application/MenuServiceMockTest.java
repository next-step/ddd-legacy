package kitchenpos.application;

import kitchenpos.ApplicationMockTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("메뉴 서비스 테스트")
@ApplicationMockTest
class MenuServiceMockTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
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
        메뉴그룹_추천메뉴 = menuGroupResponse(이름_추천메뉴);
        상품_양념치킨 = productResponse(이름_양념치킨, 가격_20000);
        상품_후라이드치킨 = productResponse(이름_후라이드치킨, 가격_18000);
        양념치킨_1개 = menuProductResponse(상품_양념치킨, 1);
        후라이드치킨_1개 = menuProductResponse(상품_후라이드치킨, 1);
        ID_추천메뉴 = 메뉴그룹_추천메뉴.getId();
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
            Menu request = buildCreateRequest(이름_순살치킨, 가격_34000, 양념치킨_1개, 후라이드치킨_1개);
            commonStubForCreateMenu();
            stubMenuRepositorySave();

            // when
            Menu result = menuService.create(request);

            // then
            assertAll(
                    () -> assertThat(result.getId()).isNotNull(),
                    () -> assertThat(result.getName()).isEqualTo(이름_순살치킨),
                    () -> assertThat(result.getPrice()).isEqualTo(가격_34000),
                    () -> assertThat(result.isDisplayed()).isTrue(),
                    () -> assertThat(result.getMenuGroupId()).isEqualTo(ID_추천메뉴),
                    () -> assertThat(result.getMenuProducts()).containsExactly(양념치킨_1개, 후라이드치킨_1개)
            );
        }

        @DisplayName("[실패] 이름은 필수로 입력해야 한다.")
        @NullSource
        @ParameterizedTest
        void fail1(String name) {
            // given
            Menu request = buildCreateRequest(name, 가격_34000, 양념치킨_1개, 후라이드치킨_1개);
            commonStubForCreateMenu();

            // when
            // then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[실패] 이름은 비속어를 포함하지 않는다.")
        @ValueSource(strings = {"욕설", "비속어"})
        @ParameterizedTest
        void fail2(String name) {
            // given
            Menu request = buildCreateRequest(name, 가격_34000, 양념치킨_1개, 후라이드치킨_1개);
            commonStubForCreateMenu();

            // when
            when(purgomalumClient.containsProfanity(any())).thenReturn(true);

            // then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[실패] 메뉴가격은 필수로 입력해야하고 `0`원 이상이다.")
        @NullSource
        @MethodSource("provideInvalidPrices")
        @ParameterizedTest
        void fail3(BigDecimal price) {
            // given
            Menu request = buildCreateRequest(이름_순살치킨, price, 양념치킨_1개, 후라이드치킨_1개);

            // when
            // then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[실패] {메뉴가격}이 {모든 메뉴구성상품의 (가격*수량) 총 합}보다 비싸지 않아야 한다.")
        @ValueSource(longs = {38_001, 50_000, 100_000})
        @ParameterizedTest
        void fail4(long price) {
            // given
            Menu request = buildCreateRequest(이름_순살치킨, BigDecimal.valueOf(price), 양념치킨_1개, 후라이드치킨_1개);
            commonStubForCreateMenu();

            // when
            // then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[실패] 메뉴는 이미 등록된 하나의 `메뉴 그룹`에 반드시 속한다.")
        @Test
        void fail5() {
            // given
            Menu request = buildCreateRequest(이름_순살치킨, 가격_34000, 양념치킨_1개, 후라이드치킨_1개);

            // when
            when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[실패] 메뉴는 `메뉴구성 상품`을 `한 가지`이상 가진다.")
        @Test
        void fail6() {
            // given
            Menu request = buildCreateRequest(이름_순살치킨, 가격_34000, 양념치킨_1개, 후라이드치킨_1개);
            stubMenuGroupRepositoryFindById();

            // when
            when(productRepository.findAllByIdIn(any())).thenReturn(emptyList());

            // then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[실패] `메뉴구성 상품`은 미리 등록된 `상품`을 가진다.")
        @Test
        void fail7() {
            // given
            Menu request = buildCreateRequest(이름_순살치킨, 가격_34000, 양념치킨_1개, 후라이드치킨_1개);
            stubMenuGroupRepositoryFindById();

            // when
            when(productRepository.findAllByIdIn(any())).thenReturn(List.of(상품_후라이드치킨));

            // then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[실패] `메뉴구성 상품`은  수량이 0개 가진다.")
        @ValueSource(longs = {-1, -111, -999999})
        @ParameterizedTest
        void fail8(long quantity) {
            // given
            Menu request = buildCreateRequest(이름_순살치킨, 가격_34000, menuProductResponse(상품_양념치킨, quantity));
            stubMenuGroupRepositoryFindById();

            // when
            when(productRepository.findAllByIdIn(any())).thenReturn(List.of(상품_양념치킨));

            // then
            assertThatThrownBy(() -> menuService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
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
            Menu request = menuChangePriceRequest(가격_34000);
            stubMenuRepositoryFindById();

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
            stubMenuRepositoryFindById();

            // when
            when(menuRepository.findById(any())).thenReturn(Optional.empty());

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
            Menu request = menuChangePriceRequest(price);

            // when
            // then
            assertThatThrownBy(() -> menuService.changePrice(ID_순살치킨, request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[실패] {메뉴가격}이 {메뉴구성상품의 (가격*수량)의 총 합}보다 비싸지 않아야 한다.")
        @ValueSource(longs = {38_001, 50_000, 100_000})
        @ParameterizedTest
        void fail3(long price) {
            // given
            Menu request = buildCreateRequest(이름_순살치킨, BigDecimal.valueOf(price), 양념치킨_1개, 후라이드치킨_1개);
            stubMenuRepositoryFindById();

            // when
            // then
            assertThatThrownBy(() -> menuService.changePrice(ID_순살치킨, request))
                    .isInstanceOf(IllegalArgumentException.class);
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
            stubMenuRepositoryFindById();

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
            stubMenuRepositoryFindById();

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
            when(menuRepository.findById(any())).thenReturn(Optional.empty());

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
            Menu menu = buildMenuResponse(price);
            UUID menuId = menu.getId();
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // when
            // then
            assertThatThrownBy(() -> menuService.display(menuId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("[성공] 메뉴 목록을 볼 수 있다.")
    @Test
    void getMenus() {
        // given
        List<Menu> list = List.of(메뉴_순살치킨);
        when(menuRepository.findAll()).thenReturn(list);

        // when
        List<Menu> result = menuService.findAll();

        // then
        assertThat(result).containsOnly(메뉴_순살치킨);
    }

    @NotNull
    private Menu buildMenuResponse(long price) {
        return menuResponse(이름_순살치킨, BigDecimal.valueOf(price), ID_추천메뉴, true, 양념치킨_1개);
    }

    private Menu buildCreateRequest(String name, BigDecimal price, MenuProduct... menuProducts) {
        return menuCreateRequest(name, price, ID_추천메뉴, true, menuProducts);
    }

    private void stubMenuRepositorySave() {
        when(menuRepository.save(any())).thenReturn(메뉴_순살치킨);
    }

    private void stubMenuGroupRepositoryFindById() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
    }

    private void stubMenuRepositoryFindById() {
        when(menuRepository.findById(any())).thenReturn(Optional.of(메뉴_순살치킨));
    }

    private void commonStubForCreateMenu() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.ofNullable(메뉴그룹_추천메뉴));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(상품_양념치킨, 상품_후라이드치킨));
        when(productRepository.findById(상품_양념치킨.getId())).thenAnswer(invocation -> Optional.ofNullable(상품_양념치킨));
        when(productRepository.findById(상품_후라이드치킨.getId())).thenAnswer(invocation -> Optional.ofNullable(상품_후라이드치킨));
    }
}
