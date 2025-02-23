package kitchenpos.application;

import static java.math.BigDecimal.valueOf;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import static kitchenpos.fixtures.MenuFixtures.createMenu;
import static kitchenpos.fixtures.MenuFixtures.createMenuGroup;
import static kitchenpos.fixtures.MenuFixtures.createMenuProduct;
import static kitchenpos.fixtures.MenuFixtures.createMenuWithoutMenuGroupId;
import static kitchenpos.fixtures.MenuFixtures.menuGroup;
import static kitchenpos.fixtures.ProductFixtures.createProduct;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

class MenuServiceTest {

    private MenuService menuService;
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;
    private Product product;
    private UUID menuGroupId;

    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        purgomalumClient = new FakePurgomalumClient();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
        product = productRepository.save(createProduct("후라이드치킨", valueOf(16000)));
        menuGroupId = menuGroupRepository.save(menuGroup()).getId();
    }

    @Test
    void 메뉴를_등록할_수_있다() {
        // given
        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);
        MenuGroup menuGroup = menuGroupRepository.findById(menuGroupId).orElseThrow();
        Menu expected = createMenu("후라이드치킨", valueOf(16000), true, menuGroup, menuProducts);

        // when
        Menu actual = menuService.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getMenuGroup()).isEqualTo(expected.getMenuGroup()),
            () -> assertThat(actual.getMenuProducts()).hasSize(expected.getMenuProducts().size())
        );
    }

    @DisplayName("메뉴에 가격이 0원 미만이면 등록할 수 없다.")
    @Test
    void 메뉴_가격이_0원_미만이면_등록할_수_없다() {
        // given
        Product negativePriceProduct = createProduct("후라이드치킨", valueOf(-16000));
        productRepository.save(negativePriceProduct);

        MenuProduct menuProduct = createMenuProduct(negativePriceProduct.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = menuGroupRepository.findById(menuGroupId).orElseThrow();
        Menu expected = createMenu("후라이드치킨", valueOf(-16000), true, menuGroup, menuProducts);

        // when & then
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("가격이 존재하거나 0원 이상이어야 합니다.");
    }

    @Test
    void 특정_메뉴_그룹이_존재하지_않으면_등록할_수_없다() {
        // given
        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        Menu expected = createMenuWithoutMenuGroupId("후라이드치킨", valueOf(16000), true, menuProducts);

        // when & then
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("메뉴는 특정 메뉴 그룹에 속해야 한다.");
    }

    @Test
    void 메뉴에_포함된_상품이_없으면_등록할_수_없다() {
        // given
        MenuGroup menuGroup = createMenuGroup("후라이드치킨");
        menuGroupRepository.save(menuGroup);

        List<MenuProduct> emptyMenuProducts = List.of();

        Menu expected = createMenu("후라이드치킨", valueOf(16000), true, menuGroup, emptyMenuProducts);

        // when & then
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴에 포함된 상품이 존재해야 합니다.");
    }

    @Test
    void 메뉴에_등록된_상품의_수량이_0개_미만이면_등록할_수_없다() {
        // given
        Product product = createProduct("후라이드치킨", valueOf(16000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), -1L);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("후라이드치킨", valueOf(16000), true, menuGroup, menuProducts);

        // when & then
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void 메뉴_이름이_없으면_등록할_수_없다(String invalidMenuName) {
        // given
        Product product = createProduct("후라이드치킨", valueOf(16000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        // when
        Menu expected = createMenu(invalidMenuName, valueOf(16000), true, menuGroup, menuProducts);

        // then
        Assertions.assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("올바른 메뉴 이름을 입력해야 합니다.");
    }

    @Test
    void 메뉴에_있는_가격을_상품들의_총_가격_이하로_변경할_수_있다() {
        // given
        Product product = createProduct("후라이드치킨", valueOf(16000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("후라이드치킨", valueOf(16000), true, menuGroup, menuProducts);
        Menu actual = menuService.create(expected);

        // when
        actual.setPrice(valueOf(9999));
        Menu changePriceMenu = menuService.changePrice(actual.getId(), actual);

        // then
        assertThat(changePriceMenu.getPrice()).isEqualTo(valueOf(9999));

    }

    @Test
    void 메뉴_가격이_0원_미만이면_변경할_수_없다() {
        // given
        Product product = createProduct("후라이드치킨", valueOf(16000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("후라이드치킨", valueOf(16000), true, menuGroup, menuProducts);
        Menu actual = menuService.create(expected);

        // when
        actual.setPrice(valueOf(-1));

        // then
        assertThatThrownBy(() -> menuService.changePrice(actual.getId(), actual))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 변경시 가격이 0원 이상이어야 합니다.");
    }

    @Test
    void 메뉴_가격이_상품_가격_합보다_크면_변경할_수_없다() {
        // given
        Product product = createProduct("후라이드치킨", valueOf(16000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("후라이드치킨", valueOf(16000), true, menuGroup, menuProducts);
        Menu actual = menuService.create(expected);

        // when
        actual.setPrice(valueOf(16001));

        // then
        assertThatThrownBy(() -> menuService.changePrice(actual.getId(), actual))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 가격은 포함된 상품 가격 합보다 클 수 없습니다.");
    }

    @Test
    void 메뉴를_표시_상태로_변경할_수_있다() {
        // given
        Product product = createProduct("후라이드치킨", valueOf(16000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("후라이드치킨", valueOf(16000), true, menuGroup, menuProducts);
        Menu actual = menuService.create(expected);

        // when
        Menu displayMenu = menuService.display(actual.getId());

        // then
        assertThat(displayMenu.isDisplayed()).isTrue();
    }

    @Test
    void 존재하지_않는_메뉴를_표시할_수_없다() {
        // given
        UUID NonExistedMenuId = UUID.randomUUID();

        // when & then
        assertThatThrownBy(() -> menuService.display(NonExistedMenuId))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("해당 ID의 메뉴가 존재하지 않습니다.");
    }

    @Test
    void 메뉴_가격이_상품_가격_합보다_높으면_메뉴를_표시할_수_없다() {
        // given
        Product product = createProduct("후라이드치킨", valueOf(16000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        // 상품 가격보다 높은 메뉴 가격 설정
        Menu expected = createMenu("후라이드치킨", valueOf(16000), false, menuGroup, menuProducts);
        Menu actual = menuService.create(expected);

        // when
        actual.setPrice(valueOf(16001));
        menuRepository.save(actual);

        // then
        assertThatThrownBy(() -> menuService.display(actual.getId()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("메뉴 가격이 포함된 상품 가격보다 높아 표시할 수 없습니다.");
    }

    @Test
    void 등록된_메뉴를_숨길_수_있다() {
        // given
        Product product = createProduct("후라이드치킨", valueOf(16000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("메인 메뉴");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("후라이드치킨", valueOf(16000), true, menuGroup, menuProducts);

        // when
        Menu actual = menuService.create(expected);
        Menu hidMenu = menuService.hide(actual.getId());

        // then
        assertThat(hidMenu.isDisplayed()).isFalse();
    }

    @Test
    void 등록된_메뉴를_모두_조회할_수_있다() {
        // given
        Product product = createProduct("후라이드치킨", valueOf(16000));
        productRepository.save(product);

        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = List.of(menuProduct);

        MenuGroup menuGroup = createMenuGroup("한식");
        menuGroupRepository.save(menuGroup);

        Menu expected = createMenu("후라이드치킨", valueOf(16000), true, menuGroup, menuProducts);
        menuService.create(expected);

        // when
        List<Menu> findAllMenu = menuService.findAll();

        // then
        assertThat(findAllMenu).isNotEmpty();
        assertThat(findAllMenu.size()).isEqualTo(1);
    }
}
