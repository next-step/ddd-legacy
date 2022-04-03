package kitchenpos.application;

import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.domain.*;
import kitchenpos.exception.MenuNameException;
import kitchenpos.exception.MenuPriceException;
import kitchenpos.inMemory.InMemoryMenuGroupRepository;
import kitchenpos.inMemory.InMemoryMenuRepository;
import kitchenpos.inMemory.InMemoryProductRepository;
import kitchenpos.infra.FakeProfanityClient;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MenuServiceTest {

    private final MenuRepository menuRepository = new InMemoryMenuRepository();

    private static final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private static final ProductRepository productRepository = new InMemoryProductRepository();

    private final ProfanityClient purgomalumClient = new FakeProfanityClient();

    private static MenuService menuService;

    private static final String 세트_메뉴 = "세트메뉴;";
    private static final String 족보_세트= "족보세트;";

    @BeforeEach
    void setUp() throws Exception {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("메뉴는 반드시 고유 ID, 메뉴 그룹, 메뉴명, 가격, 표시 여부, 메뉴 상품목록이 있어야 한다.")
    @Test
    void create() {
        // given
        final Menu menu = new Menu();

        final MenuGroup 세트메뉴 = menuGroupRepository.save(MenuGroupServiceTest.createMenuGroup(세트_메뉴));
        menu.setMenuGroupId(세트메뉴.getId());

        menu.setName(족보_세트);
        menu.setPrice(BigDecimal.valueOf(52_000L));
        menu.setDisplayed(Boolean.TRUE);

        final List<MenuProduct> listMenuProducts = new ArrayList<>();
        final Product 보쌈_중짜 = productRepository.save(ProductFixture.createMetaProduct("보쌈(중)", 24_000));
        final Product 족발_중짜 =  productRepository.save(ProductFixture.createMetaProduct("족발(중)", 23_000));
        final Product 막국수 =  productRepository.save(ProductFixture.createMetaProduct("막국수", 5_000));
        listMenuProducts.add(MenuProductFixture.create(보쌈_중짜, 1));
        listMenuProducts.add(MenuProductFixture.create(족발_중짜, 1));
        listMenuProducts.add(MenuProductFixture.create(막국수, 1));
        menu.setMenuProducts(listMenuProducts);

        // when
        final Menu actual = menuService.create(menu);

        // then
        Assertions.assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getMenuGroup()).isNotNull(),
                () -> assertThat(actual.getName()).isNotNull(),
                () -> assertThat(actual.getPrice()).isNotEqualTo(BigDecimal.ZERO),
                () -> assertThat(actual.isDisplayed()).isEqualTo(Boolean.TRUE),
                () -> assertThat(actual.getMenuProducts()).isNotEmpty()
        );

    }

    @DisplayName("[추가] 메뉴는 반드시 이름을 갖는다.")
    @Test
    void checkMenuName() {
        // given
        final Menu menu = getSimpleMenuRequest("세트메뉴", null, 23_000);

        // then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(MenuNameException.class);

    }

    @DisplayName("비속어 사이트에 등록된 단어로 메뉴명을 사용할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"욕설", "비속어가 포함된 단어"})
    void create(final String menuName) {

        // given
        final Menu menu = getSimpleMenuRequest("세트메뉴", menuName, 52_000);

        // then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(MenuNameException.class);

    }

    @DisplayName("등록한 모든 메뉴를 볼 수 있다.")
    @Test
    void findAll() {
        menuRepository.save(getSimpleMenuRequest(세트_메뉴, 족보_세트, 52_000));
        menuRepository.save(getSimpleMenuRequest("사이드메뉴", "치즈볼감자", 12_000));

        final List<Menu> actual = menuService.findAll();
        assertThat(actual).hasSize(2);
    }

    @DisplayName("등록된 상품의 총 가격보다 작은 메뉴는 노출한다.")
    @Test
    void display() {
        final Menu menu = menuRepository.save(getSimpleMenuRequest(세트_메뉴, 족보_세트, 19_000));
        assertThat(menuService.display(menu.getId())
                .isDisplayed())
                .isEqualTo(Boolean.TRUE);
    }

    @DisplayName("메뉴를 숨긴다.")
    @Test
    void hide() {
        final Menu menu = menuRepository.save(getSimpleMenuRequest(세트_메뉴, 족보_세트, 52_000));
        assertThat(menuService.hide(menu.getId())
                .isDisplayed())
                .isEqualTo(Boolean.FALSE);
    }

    @DisplayName("등록한 메뉴의 표시 가격을 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"19000"})
    void changePrice(BigDecimal price) {

        // given
        final Menu menu = menuService.create(getSimpleMenuRequest(세트_메뉴, 족보_세트, 45_000));

        // when
        menu.setPrice(price);

        // then
        final Menu changedMenu = menuService.changePrice(menu.getId(), menu);
        assertThat(changedMenu.getPrice()).isEqualTo(price);
    }

    @DisplayName("메뉴 가격은 메뉴를 구성하는 상품목록의 총 금액 보다 비싸야 한다.")
    @ParameterizedTest
    @ValueSource(ints = {59_000, 62_000})
    void cannotCreateMenu(int price) {
        final Menu menu = getSimpleMenu(price);
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(MenuPriceException.class);
    }

    static Menu getSimpleMenu(int price) {
        return getSimpleMenuRequest(세트_메뉴, 족보_세트, price);
    }

    static Menu getSimpleMenuRequest(String menuGroupName, String menuName, int price) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());

        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupServiceTest.createMenuGroup(menuGroupName));
        menu.setMenuGroupId(menuGroup.getId());

        menu.setName(menuName);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(Boolean.TRUE);

        final List<MenuProduct> listMenuProducts = new ArrayList<>();
        final Product 보쌈_중짜 = productRepository.save(ProductFixture.createMetaProduct("보쌈(중)", 24_000));
        final Product 족발_중짜 =  productRepository.save(ProductFixture.createMetaProduct("족발(중)", 23_000));
        listMenuProducts.add(MenuProductFixture.create(보쌈_중짜, 1));
        listMenuProducts.add(MenuProductFixture.create(족발_중짜, 1));
        menu.setMenuProducts(listMenuProducts);

        return menu;
    }

}
