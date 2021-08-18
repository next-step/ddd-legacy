package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.FakePurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kitchenpos.domain.MenuFixture.createMenuProduct;
import static kitchenpos.domain.MenuFixture.createMenuRepository;
import static kitchenpos.domain.MenuGroupFixture.createMenuGroupRepository;
import static kitchenpos.domain.ProductFixture.createProductRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuServiceTest {

    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private MenuService menuService;
    private Menu menu;

    @BeforeEach
    void setUp() {
        menuGroupRepository = createMenuGroupRepository();
        productRepository = createProductRepository();
        menuRepository = createMenuRepository(menuGroupRepository, productRepository);
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, new FakePurgomalumClient());
        menu = createMenu();
    }

    @DisplayName("새로운 메뉴를 추가할 수 있다.")
    @Test
    void create() {
        // given
        final Menu createdMenu = menuService.create(menu);

        //then
        assertAll(
                () -> assertThat(createdMenu.getName())
                        .isEqualTo(createdMenu.getName()),
                () -> assertThat(createdMenu.getPrice())
                        .isEqualTo(createdMenu.getPrice()),
                () -> assertThat(createdMenu.getMenuGroup())
                        .isEqualTo(createdMenu.getMenuGroup()),
                () -> assertThat(createdMenu.isDisplayed())
                        .isEqualTo(createdMenu.isDisplayed()));
    }

    @DisplayName("가격이 없는 메뉴는 추가할 수 없다.")
    @Test
    void create_emptyPrice() {
        // given
        menu.setPrice(null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("가격이 음수인 메뉴는 추가할 수 없다.")
    @Test
    void create_negativePrice() {
        // given
        menu.setPrice(BigDecimal.valueOf(-1));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 그룹에 속하지 않은 메뉴는 추가할 수 없다.")
    @Test
    void create_emptyMenuGroup() {
        // given
        menu.setMenuGroupId(null);

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("기존 메뉴 그룹에 속하지 않은 메뉴는 추가할 수 없다.")
    @Test
    void create_badMenuGroup() {
        // given
        menu.setMenuGroupId(UUID.randomUUID());

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴는 반드시 상품을 포함해야 한다.")
    @MethodSource
    @ParameterizedTest
    void create_emptyProduct(final List<MenuProduct> menuProducts) {
        // given
        menu.setMenuProducts(menuProducts);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    private static Stream<List<MenuGroup>> create_emptyProduct() {
        return Stream.of(null, Collections.emptyList());
    }

    @DisplayName("메뉴를 구성하는 상품은 이미 추가된 상품이어야 한다.")
    @Test
    void create_badProduct() {
        // given
        final List<MenuProduct> menuProducts = Collections.singletonList(new MenuProduct());
        menu.setMenuProducts(menuProducts);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 상품 수량은 음수일 수 없다.")
    @Test
    void create_negativeProductQuantity() {
        // given
        final int quantity = -1;
        final Product product = productRepository.findAll().get(0);
        final List<MenuProduct> menuProducts = Collections.singletonList(createMenuProduct(product, quantity));
        menu.setMenuProducts(menuProducts);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 가격은 각 상품가격의 총합보다 작아야 한다.")
    @Test
    void create_expensive() {
        // given
        final BigDecimal price = BigDecimal.valueOf(1_000_000);
        menu.setPrice(price);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 이름은 필수값이며, 비어있을 수 없다.")
    @Test
    void create_emptyName() {
        // given
        menu.setName(null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 이름은 저속해서는 안된다.")
    @Test
    void create_badName() {
        // given
        menu.setName("비속어 욕설 메뉴");

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 가격을 수정할 수 있다.")
    @Test
    void changePrice() {
        // given
        final Menu menu = getMenu();
        final BigDecimal changedPrice = BigDecimal.valueOf(20);
        final Menu changedMenu = createMenu();
        changedMenu.setPrice(changedPrice);

        // when
        menuService.changePrice(menu.getId(), changedMenu);

        // then
        assertThat(menu.getPrice())
                .isEqualTo(changedPrice);
    }

    @DisplayName("메뉴의 가격을 수정할 때 가격을 반드시 입력해야한다.")
    @Test
    void changePrice_emptyPrice() {
        // given
        final Menu menu = getMenu();
        final Menu changedMenu = createMenu();
        changedMenu.setPrice(null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(menu.getId(), changedMenu));
    }

    @DisplayName("메뉴의 가격을 수정할 때 가격을 음수로 입력할 수 없다.")
    @Test
    void changePrice_negativePrice() {
        // given
        final Menu menu = getMenu();
        final Menu changedMenu = createMenu();
        changedMenu.setPrice(BigDecimal.valueOf(-1));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(menu.getId(), changedMenu));
    }

    @DisplayName("메뉴의 가격을 수정할 때, 수정할 메뉴를 필수적으로 지정해야한다.")
    @Test
    void changePrice_emptyMenu() {
        // given
        final UUID menuId = null;

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.changePrice(menuId, createMenu()));
    }

    @DisplayName("가격을 수정할 메뉴는 이미 추가되어 있는 메뉴여야 한다.")
    @Test
    void changePrice_badMenu() {
        // given
        final UUID menuId = UUID.randomUUID();

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.changePrice(menuId, createMenu()));
    }

    @DisplayName("수정된 메뉴의 가격은 각 상품 가격의 총합보다 작아야 한다.")
    @Test
    void changePrice_expensivePrice() {
        // given
        final Menu menu = getMenu();
        final Menu changedMenu = createMenu();
        changedMenu.setPrice(BigDecimal.valueOf(1_000_000));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(menu.getId(), changedMenu));
    }

    @DisplayName("메뉴가 보여지도록 할 수 있다.")
    @Test
    void display() {
        // given
        final Menu menu = getMenu();
        menu.setDisplayed(false);

        // when
        menuService.display(menu.getId());

        // then
        assertThat(menu.isDisplayed())
                .isTrue();
    }

    @DisplayName("보여질 메뉴는 필수적으로 지정해야한다.")
    @Test
    void display_emptyMenu() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.display(null));
    }

    @DisplayName("보여질 메뉴는 이미 추가되어 있어야 한다.")
    @Test
    void display_badMenu() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.display(UUID.randomUUID()));
    }

    @DisplayName("메뉴가 숨겨지도록 할 수 있다.")
    @Test
    void hide() {
        // given
        final Menu menu = getMenu();
        menu.setDisplayed(true);

        // when
        menuService.hide(menu.getId());

        // then
        assertThat(menu.isDisplayed())
                .isFalse();
    }

    @DisplayName("숨겨질 메뉴는 필수적으로 지정해야한다.")
    @Test
    void hide_emptyMenu() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.hide(null));
    }

    @DisplayName("숨겨질 메뉴는 이미 추가되어 있어야 한다.")
    @Test
    void findAll() {
        // given
        final List<String> expectedNames = menuRepository.findAll().stream()
                .map(Menu::getName)
                .collect(Collectors.toList());

        // when
        final List<String> actualNames = menuService.findAll().stream()
                .map(Menu::getName)
                .collect(Collectors.toList());

        // then
        assertThat(actualNames)
                .isEqualTo(expectedNames);
    }

    @DisplayName("메뉴들의 목록을 조회할 수 있다.")
    @Test
    void hide_badMenu() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.hide(UUID.randomUUID()));
    }

    private Menu createMenu() {
        final String name = "신메뉴";
        final MenuGroup menuGroup = menuGroupRepository.findAll().get(0);
        final List<Product> products = productRepository.findAll();
        return MenuFixture.createMenu(name, menuGroup, products);
    }

    private Menu getMenu() {
        return menuRepository.findAll().get(0);
    }
}
