package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.application.ProductServiceTest.상품만들기;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuServiceTest {
    private MenuService menuService;
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    @BeforeEach
    void setUp() {
        menuService = new MenuService(MenuFixture.menuRepository, MenuGroupFixture.menuGroupRepository, ProductFixture.productRepository, purgomalumClient);
    }

    @AfterEach
    void cleanUp() {
        MenuFixture.비우기();
        MenuGroupFixture.비우기();
        ProductFixture.비우기();
    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void create() {
        final Menu menu = MenuFixture.메뉴();
        final Menu saved = 메뉴등록(menu);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getName()).isEqualTo(menu.getName()),
                () -> assertThat(saved.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(saved.getMenuProducts()).hasSize(2),
                () -> assertThat(saved.getMenuGroup()).isEqualTo(menu.getMenuGroup()),
                () -> assertThat(saved.isDisplayed()).isTrue()
        );
    }

    @DisplayName("메뉴의 가격은 0 이상이어야하고, 상품들의 가격의 총합을 넘을 수 없다.")
    @ValueSource(strings = {"-1000", "1000000"})
    @NullSource
    @ParameterizedTest
    void create(BigDecimal price) {
        final Menu menu = MenuFixture.메뉴();
        menu.setPrice(price);

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름은 비어있거나 욕설이 아니어야한다.")
    @ValueSource(strings = "욕설")
    @NullAndEmptySource
    @ParameterizedTest
    void create(String name) {
        final Menu menu = MenuFixture.메뉴();
        menu.setName(name);

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 메뉴상품은 한 가지 이상이어야한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void create_MenuProduct(List menuProducts) {
        final Menu menu = MenuFixture.메뉴();
        menu.setMenuProducts(menuProducts);

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 메뉴상품은 등록된 상품이어야한다.")
    @Test
    void create_MenuProduct_without_Product() {
        final Menu menu = MenuFixture.메뉴();
        menu.getMenuProducts().add(MenuFixture.등록되지않은_메뉴상품());

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 메뉴상품 수량은 0보다 커야한다.")
    @Test
    void create_MenuProduct_quantity() {
        final Menu menu = MenuFixture.메뉴();
        menu.getMenuProducts().add(MenuFixture.수량이음수인_메뉴상품());

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        final Menu saved = MenuFixture.메뉴저장();
        final Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(8_000L));

        final Menu expected = 메뉴가격변경(saved.getId(), request);

        assertAll(
                () -> assertThat(expected.getPrice()).isEqualTo(request.getPrice()),
                () -> assertThat(expected.getId()).isEqualTo(saved.getId()),
                () -> assertThat(expected.getName()).isEqualTo(saved.getName())
        );
    }

    @DisplayName("메뉴의 가격을 변경할 땐 0보다 큰 메뉴상품의 총합을 넘지 않는 값이어야한다.")
    @ValueSource(strings = {"-1000", "1000000"})
    @NullSource
    @ParameterizedTest
    void changePrice(BigDecimal price) {
        final Menu saved = MenuFixture.메뉴저장();
        final Menu request = new Menu();
        request.setPrice(price);

        assertThatThrownBy(() -> 메뉴가격변경(saved.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 노출할 수 있다.")
    @Test
    void display() {
        final Menu menu = MenuFixture.메뉴();
        menu.setDisplayed(false);
        final Menu saved = MenuFixture.메뉴저장(menu);
        assertThat(saved.isDisplayed()).isFalse();

        final Menu expected = 메뉴노출(saved.getId());
        assertThat(expected.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 노출할땐 가격이 메뉴상품의 가격합보다 커서는 안된다.")
    @Test
    void display_price() {
        final Menu menu = MenuFixture.메뉴();
        menu.setDisplayed(false);
        menu.setPrice(BigDecimal.valueOf(12_000L));
        final Menu saved = MenuFixture.메뉴저장(menu);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 메뉴노출(saved.getId()));
    }

    @DisplayName("메뉴를 숨길 수 있다.")
    @Test
    void hide() {
        final Menu saved = MenuFixture.메뉴저장();

        final Menu expected = menuService.hide(saved.getId());
        assertThat(expected.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 전체조회할 수 있다.")
    @Test
    void findAll(){
        final Menu saved1 = MenuFixture.메뉴저장();
        final Menu saved2 = MenuFixture.메뉴저장();

        assertThat(메뉴전체조회()).containsOnly(saved1, saved2);
    }


    private Menu 메뉴등록(Menu menu) {
        return menuService.create(menu);
    }

    private Menu 메뉴가격변경(UUID id, Menu menu) {
        return menuService.changePrice(id, menu);
    }

    private Menu 메뉴노출(UUID id) {
        return menuService.display(id);
    }

    private List<Menu> 메뉴전체조회() {
        return menuService.findAll();
    }

    public static Menu 메뉴만들기(MenuRepository menuRepository, MenuGroupRepository menuGroupRepository, ProductRepository productRepository) {
        MenuGroup menuGroup = 메뉴그룹만들기(menuGroupRepository);
        List<MenuProduct> menuProducts = 메뉴상품들만들기(productRepository);
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("메뉴");
        menu.setPrice(BigDecimal.valueOf(10_000L));
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        return menuRepository.save(menu);
    }

    public static List<MenuProduct> 메뉴상품들만들기(ProductRepository productRepository) {
        final Product product = 상품만들기(productRepository);

        final MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProduct(product);
        menuProduct1.setProductId(product.getId());
        menuProduct1.setQuantity(1L);

        final MenuProduct menuProduct2 = new MenuProduct();
        menuProduct2.setProduct(product);
        menuProduct2.setProductId(product.getId());
        menuProduct2.setQuantity(2L);
        return new ArrayList<>(Arrays.asList(menuProduct1, menuProduct2));
    }

    public static MenuGroup 메뉴그룹만들기(MenuGroupRepository menuGroupRepository) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("메뉴 그룹");
        return menuGroupRepository.save(menuGroup);
    }

}
