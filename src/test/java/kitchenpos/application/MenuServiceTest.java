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
import static kitchenpos.fixture.MenuFixture.NAME_순살치킨;
import static kitchenpos.fixture.MenuFixture.PRICE_32000;
import static kitchenpos.fixture.MenuFixture.menuChangePriceRequest;
import static kitchenpos.fixture.MenuFixture.menuCreateRequest;
import static kitchenpos.fixture.MenuFixture.menuResponse;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupResponse;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.ProductFixture.NAME_강정치킨;
import static kitchenpos.fixture.ProductFixture.NAME_후라이드치킨;
import static kitchenpos.fixture.ProductFixture.PRICE_17000;
import static kitchenpos.fixture.ProductFixture.PRICE_18000;
import static kitchenpos.fixture.ProductFixture.productResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("메뉴 서비스 테스트")
@ApplicationMockTest
class MenuServiceTest {
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

    private MenuGroup MENU_GROUP_추천메뉴;
    private Product PRODUCT_강정치킨;
    private Product PRODUCT_후라이드치킨;
    private MenuProduct 강정치킨_1개;
    private MenuProduct 후라이드치킨_1개;
    private UUID ID_MENU_GOURP_추천메뉴;
    private Menu MENU_순살치킨;
    private UUID ID_MENU_순살치킨;

    @BeforeEach
    void setUp() {
        MENU_GROUP_추천메뉴 = menuGroupResponse("추천메뉴");
        PRODUCT_강정치킨 = productResponse(NAME_강정치킨, PRICE_17000);
        PRODUCT_후라이드치킨 = productResponse(NAME_후라이드치킨, PRICE_18000);
        강정치킨_1개 = menuProductResponse(1L, PRODUCT_강정치킨, 1);
        후라이드치킨_1개 = menuProductResponse(2L, PRODUCT_후라이드치킨, 1);
        ID_MENU_GOURP_추천메뉴 = MENU_GROUP_추천메뉴.getId();
        MENU_순살치킨 = menuResponse(NAME_순살치킨, PRICE_32000, ID_MENU_GOURP_추천메뉴, true, 강정치킨_1개, 후라이드치킨_1개);
        ID_MENU_순살치킨 = MENU_순살치킨.getId();
    }

    @DisplayName("메뉴를 등록한다.")
    @Test
    void creatMenu() {
        // given
        Menu request = buildCreateRequest();
        commonStubForCreateMenu();
        stubMenuRepositorySave();

        // when
        Menu result = menuService.create(request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo(NAME_순살치킨),
                () -> assertThat(result.getPrice()).isEqualTo(PRICE_32000),
                () -> assertThat(result.isDisplayed()).isTrue(),
                () -> assertThat(result.getMenuGroupId()).isEqualTo(ID_MENU_GOURP_추천메뉴),
                () -> assertThat(result.getMenuProducts()).hasSize(2)
                        .containsExactly(강정치킨_1개, 후라이드치킨_1개)
        );
    }

    @DisplayName("메뉴를 등록할 때, 이름이 공백이면 예외가 발생한다.")
    @NullSource
    @ParameterizedTest
    void creatMenu_nullNameException(String name) {
        // given
        Menu request = buildCreateRequest(name);
        commonStubForCreateMenu();

        // when
        // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록할 때, 이름에 비속어가 포함되면 예외가 발생한다.")
    @ValueSource(strings = {"욕설", "비속어"})
    @ParameterizedTest
    void creatMenu_containProfanityNameException(String name) {
        // given
        Menu request = buildCreateRequest(name);
        commonStubForCreateMenu();

        // when
        when(purgomalumClient.containsProfanity(any())).thenReturn(true);

        // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록할 때, 메뉴 가격이 공백이거나 0원보다 작으면 예외가 발생한다.")
    @NullSource
    @MethodSource("provideInvalidPrices")
    @ParameterizedTest
    void creatMenu_NullOrNegativePriceException(BigDecimal price) {
        // given
        Menu request = buildCreateRequest(price);

        // when
        // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록할 때, 메뉴가격이 메뉴구성상품의 (가격*수량)의 총 합보다 비싸면 예외 발생한다.")
    @ValueSource(longs = {35_001, 50_000, 100_000})
    @ParameterizedTest
    void creatMenu_invalidPricePolicyException(long price) {
        // given
        Menu request = buildCreateRequest(BigDecimal.valueOf(price));
        commonStubForCreateMenu();

        // when
        // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록할 때, 미리 등록되어 있지 않은 메뉴그룹이면 예외 발생한다.")
    @Test
    void creatMenu_notExistsMenuGroupException() {
        // given
        Menu request = buildCreateRequest();

        // when
        when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴를 등록할 때, 메뉴구성상품을 하나도 담지 않으면 예외 발생한다.")
    @Test
    void creatMenu_emptyMenuProductException() {
        // given
        Menu request = buildCreateRequest();
        stubMenuGroupRepositoryFindById();

        // when
        when(productRepository.findAllByIdIn(any())).thenReturn(emptyList());

        // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록할 때, 메뉴구성상품이 미리 등록되지 않은 상품이 하나라도 있으면 예외 발생한다.")
    @Test
    void creatMenu_notExistsMenuProductException() {
        // given
        Menu request = buildCreateRequest();
        stubMenuGroupRepositoryFindById();

        // when
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(PRODUCT_후라이드치킨));

        // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록할 때, 메뉴구성상품의 수량이 0개 미만이면 예외 발생한다.")
    @ValueSource(longs = {-1, -111, -999999})
    @ParameterizedTest
    void creatMenu_negativeQuantityMenuProductException(long quantity) {
        // given
        Menu request = buildCreateRequest(menuProductResponse(PRODUCT_강정치킨, quantity));
        stubMenuGroupRepositoryFindById();

        // when
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(PRODUCT_강정치킨));

        // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격을 수정한다.")
    @Test
    void changeMenuPrice() {
        // given
        Menu request = menuChangePriceRequest(BigDecimal.valueOf(30_000));
        when(menuRepository.findById(any())).thenReturn(Optional.of(MENU_순살치킨));

        // when
        Menu result = menuService.changePrice(ID_MENU_순살치킨, request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isEqualTo(ID_MENU_순살치킨),
                () -> assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(30_000))
        );
    }

    @DisplayName("메뉴의 가격을 수정할 때, 가격이 공백이거나  0원보다 작으면 예외가 발생한다.")
    @NullSource
    @MethodSource("provideInvalidPrices")
    @ParameterizedTest
    void changeMenuPrice_NullOrNegativePriceException(BigDecimal price) {
        // given
        Menu request = menuChangePriceRequest(price);

        // when
        // then
        assertThatThrownBy(() -> menuService.changePrice(ID_MENU_순살치킨, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격을 수정할 때, 메뉴가격이 메뉴구성상품의 (가격*수량)의 총 합보다 비싸면 예외 발생한다.")
    @ValueSource(longs = {35_001, 50_000, 100_000})
    @ParameterizedTest
    void changeMenuPrice_invalidPricePolicyException(long price) {
        // given
        Menu request = buildCreateRequest(BigDecimal.valueOf(price));
        when(menuRepository.findById(any())).thenReturn(Optional.of(MENU_순살치킨));

        // when
        // then
        assertThatThrownBy(() -> menuService.changePrice(ID_MENU_순살치킨, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @NotNull
    private Menu buildCreateRequest() {
        return menuCreateRequest(NAME_순살치킨, PRICE_32000, ID_MENU_GOURP_추천메뉴, true, 강정치킨_1개, 후라이드치킨_1개);
    }

    @NotNull
    private Menu buildCreateRequest(String name) {
        return menuCreateRequest(name, PRICE_32000, ID_MENU_GOURP_추천메뉴, true, 강정치킨_1개, 후라이드치킨_1개);
    }

    @NotNull
    private Menu buildCreateRequest(BigDecimal price) {
        return menuCreateRequest(NAME_순살치킨, price, ID_MENU_GOURP_추천메뉴, true, 강정치킨_1개, 후라이드치킨_1개);
    }

    @NotNull
    private Menu buildCreateRequest(MenuProduct... menuProducts) {
        return menuCreateRequest(NAME_순살치킨, PRICE_32000, ID_MENU_GOURP_추천메뉴, true, menuProducts);
    }

    private void stubMenuRepositorySave() {
        when(menuRepository.save(any())).thenReturn(MENU_순살치킨);
    }

    private void stubMenuGroupRepositoryFindById() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));
    }

    private void commonStubForCreateMenu() {
        when(menuGroupRepository.findById(any())).thenReturn(Optional.ofNullable(MENU_GROUP_추천메뉴));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(PRODUCT_강정치킨, PRODUCT_후라이드치킨));
        when(productRepository.findById(PRODUCT_강정치킨.getId())).thenAnswer(invocation -> Optional.ofNullable(PRODUCT_강정치킨));
        when(productRepository.findById(PRODUCT_후라이드치킨.getId())).thenAnswer(invocation -> Optional.ofNullable(PRODUCT_후라이드치킨));
    }

    private static Stream<BigDecimal> provideInvalidPrices() {
        return Stream.of(BigDecimal.valueOf(-1), BigDecimal.valueOf(-1000), BigDecimal.valueOf(-99999990));
    }
}
