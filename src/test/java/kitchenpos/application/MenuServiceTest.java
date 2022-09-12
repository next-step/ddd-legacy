package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.repository.InMemoryMenuGroupRepository;
import kitchenpos.repository.InMemoryMenuRepository;
import kitchenpos.repository.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.fixture.domain.MenuFixture.menu;
import static kitchenpos.fixture.domain.MenuGroupFixture.menuGroup;
import static kitchenpos.fixture.domain.MenuProductFixture.menuProduct;
import static kitchenpos.fixture.domain.ProductFixture.product;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;


class MenuServiceTest {

    private MenuService menuService;
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;
    private UUID menuGroupId;
    private Product product;

    @BeforeEach
    void beforeEach() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        purgomalumClient = new FakePurgomalumClient();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
        menuGroupId = menuGroupRepository.save(menuGroup()).getId();
        product = productRepository.save(product("돈코츠 라멘", 20_000L));
    }

    @Test
    @DisplayName("메뉴를 조회한다")
    void finaMenu() {
        // given
        menuRepository.save(menu());

        // when
        final List<Menu> result = menuService.findAll();

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("메뉴를 추가한다")
    void createMenu() {
        // given
        final Menu request = createMenuRequest(
                "돈코츠 라멘", 19_000L, menuGroupId, true, createMenuProductRequest(product.getId(), 2L)
        );

        // when
        final Menu result = menuService.create(request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo(request.getName()),
                () -> assertThat(result.getPrice()).isEqualTo(request.getPrice()),
                () -> assertThat(result.isDisplayed()).isTrue(),
                () -> assertThat(result.getMenuGroup().getId()).isEqualTo(request.getMenuGroupId()),
                () -> assertThat(result.getPrice()).isEqualTo(request.getPrice()),
                () -> assertThat(result.getMenuProducts()).hasSize(1)
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1.1"})
    @DisplayName("가격이 없거나, 가격이 0원 이하이면 메뉴를 추가할 수 없다.")
    void createMenuNotPrice(BigDecimal input) {
        // given
        final Menu request = createMenuRequest(
                "돈코츠 라멘", input, menuGroupId, true, createMenuProductRequest(product.getId(), 2L)
        );

        // then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(request));
    }

    @NullSource
    @ParameterizedTest
    @DisplayName("추가할 메뉴에 대해 카테고리가 존재하지 않으면 추가할 수 없다.")
    void createMenuNotFoundMenuGroup(final UUID menuGroupId) {
        // given
        final Menu request = createMenuRequest(
                "돈코츠 라멘", 19_000L, menuGroupId, true, createMenuProductRequest(product.getId(), 2L)
        );

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @Test
    @DisplayName("메뉴 상품 목록이 비어있으면 메뉴를 추가할 수 없다.")
    void createMenuNotFoundMenuProduct() {
        // given
        final Menu request = createMenuRequest("돈코츠 라멘", 19_000L, menuGroupId, true);

        // then
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(request));
    }

    @NullSource
    @ParameterizedTest
    @DisplayName(" 조회 한 상품 목록이 메뉴 상품 목록에 대해 갯수가 일치하지 않으면 메뉴를 추가할 수 없다.")
    void createMenuProductNotMatchedSizeProduct(final UUID productId) {
        // given
        final Menu request = createMenuRequest("돈코츠 라멘", 19_000L, menuGroupId, true, createMenuProductRequest(productId, 2L));

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(request));
    }

    @Test
    @DisplayName("수량이 0 미만인 상품이 있다면 메뉴를 추가할 수 없다.")
    void createMenuProductQuantityIsZeroUnder() {
        // given
        final Menu request = createMenuRequest(
                "돈코츠 라멘", 19_000L, menuGroupId, true, createMenuProductRequest(product.getId(), -1L)
        );

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(request));
    }

    @Test
    @DisplayName(" 측정 된 가격이 0원 이하면 메뉴를 추가할 수 없다.")
    void createMenuProductSetPriceZeroUnder() {
        // given
        final Menu request = createMenuRequest(
                "돈코츠 라멘", 19_000L, menuGroupId, true, createMenuProductRequest(product.getId(), 0)
        );

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(request));
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("메뉴 추가 시 이름이 비어있으면 생성할 수 없다")
    void menuCreateNotName(String input) {
        // given
        final Menu request = createMenuRequest(
                input, 19_000L, menuGroupId, true, createMenuProductRequest(product.getId(), 0)
        );

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                menuService.create(request)
        );
    }

    @Test
    @DisplayName("카테고리 생성 시 이름에 비속어가 들어가 있으면 생성할 수 없다")
    void menuCreateIsPurgomalum() {
        // given
        final Menu request = createMenuRequest(
                "맛없어", 19_000L, menuGroupId, true, createMenuProductRequest(product.getId(), 0)
        );

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                menuService.create(request)
        );
    }

    @Test
    @DisplayName("메뉴 가격을 변경한다")
    void changePrice() {
        // given
        final UUID menuId = menuRepository.save(menu()).getId();
        final Menu request = createMenuChangePriceRequest();

        // when
        final Menu result = menuService.changePrice(menuId, request);

        // then
        assertThat(result.getPrice()).isEqualTo(request.getPrice());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"-1"})
    @DisplayName("가격이 없거나, 가격이 0원 이하면 변경할 수 없다")
    void changePriceButNotPrice(BigDecimal input) {
        // given
        final UUID menuId = menuRepository.save(menu()).getId();
        final Menu request = createMenuChangePriceRequest(input);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                menuService.changePrice(menuId, request)
        );
    }

    private Menu createMenuChangePriceRequest() {
        return createMenuChangePriceRequest(13_000L);
    }

    private Menu createMenuChangePriceRequest(Long price) {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(price));
        return menu;
    }

    private Menu createMenuChangePriceRequest(BigDecimal price) {
        Menu menu = new Menu();
        menu.setPrice(price);
        return menu;
    }

    @Test
    @DisplayName("메뉴가 존재하지 않으면 가격을 변경할 수 없다")
    void changePriceButNotExistedMenu() {
        // given
        final Menu request = createMenuChangePriceRequest();

        // then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                menuService.changePrice(null, request)
        );
    }

    @Test
    @DisplayName("메뉴의 가격이 메뉴의 수량에 대한 값보다 작으면 변경할 수 없다")
    void changePriceButSumLessThenPrice() {
        // given
        final UUID menuId = menuRepository.save(menu()).getId();
        final Menu request = createMenuChangePriceRequest(50_000L);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                menuService.changePrice(menuId, request)
        );
    }

    @Test
    @DisplayName("비공개 된 메뉴를 공개할 수 있다")
    void changeDisplay() {
        // given
        final UUID menuId = menuRepository.save(menu(false)).getId();

        // when
        Menu result = menuService.display(menuId);

        // then
        assertThat(result.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName(" 메뉴가 존재하지 않으면 메뉴를 공개할 수 없다.")
    void changeDisplayNotExistedMenu() {
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                menuService.display(null)
        );
    }

    @Test
    @DisplayName("메뉴의 가격이 메뉴의 수량에 대한 값보다 작으면 공개할 수 없다")
    void changeDisplayButSumLessThenPrice() {
        // given
        final UUID menuId = menuRepository.save(
                menu(10000L, false, menuProduct(product(), 0))).getId();

        // then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() ->
                menuService.display(menuId)
        );
    }

    @Test
    @DisplayName("공개 된 메뉴를 비공개할 수 있다")
    void changeHide() {
        // given
        final UUID menuId = menuRepository.save(menu()).getId();

        // when
        Menu result = menuService.hide(menuId);

        // then
        assertThat(result.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName(" 메뉴가 존재하지 않으면 메뉴를 비공개할 수 없다.")
    void changeHideNotExistedMenu() {
        // then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                menuService.display(null)
        );
    }

    private Menu createMenuRequest(
            final String name,
            final long price,
            final UUID menuGroupId,
            final boolean displayed,
            final MenuProduct... menuProducts
    ) {
        return createMenuRequest(name, BigDecimal.valueOf(price), menuGroupId, displayed, menuProducts);
    }

    private Menu createMenuRequest(
            final String name,
            final BigDecimal price,
            final UUID menuGroupId,
            final boolean displayed,
            final MenuProduct... menuProducts
    ) {
        return createMenuRequest(name, price, menuGroupId, displayed, Arrays.asList(menuProducts));
    }

    private Menu createMenuRequest(
            final String name,
            final BigDecimal price,
            final UUID menuGroupId,
            final boolean displayed,
            final List<MenuProduct> menuProducts
    ) {
        final Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    private static MenuProduct createMenuProductRequest(final UUID productId, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
