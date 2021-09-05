package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.utils.fixture.MenuFixture;
import kitchenpos.utils.fixture.MenuGroupFixture;
import kitchenpos.utils.fixture.ProductFixture;
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuServiceTest {
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    private MenuService menuService;
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void create() {
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.메뉴그룹());
        final Product product = productRepository.save(ProductFixture.상품());
        final List<MenuProduct> menuProducts = new ArrayList<>(Arrays.asList(MenuFixture.메뉴상품(product)));
        final Menu menu = MenuFixture.메뉴(menuGroup, menuProducts);

        final Menu saved = 메뉴등록(menu);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getName()).isEqualTo(menu.getName()),
                () -> assertThat(saved.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(saved.getMenuProducts()).hasSize(1),
                () -> assertThat(saved.getMenuGroup()).isEqualTo(menu.getMenuGroup()),
                () -> assertThat(saved.isDisplayed()).isTrue()
        );
    }

    @DisplayName("메뉴의 가격은 0 이상이어야하고, 상품들의 가격의 총합을 넘을 수 없다.")
    @ValueSource(strings = {"-1000", "1000000"})
    @NullSource
    @ParameterizedTest
    void create(BigDecimal price) {
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.메뉴그룹());
        final Product product = productRepository.save(ProductFixture.상품());
        final List<MenuProduct> menuProducts = new ArrayList<>(Arrays.asList(MenuFixture.메뉴상품(product)));
        final Menu menu = MenuFixture.메뉴(menuGroup, menuProducts);
        menu.setPrice(price);

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름은 비어있거나 욕설이 아니어야한다.")
    @ValueSource(strings = "욕설")
    @NullAndEmptySource
    @ParameterizedTest
    void create(String name) {
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.메뉴그룹());
        final Product product = productRepository.save(ProductFixture.상품());
        final List<MenuProduct> menuProducts = new ArrayList<>(Arrays.asList(MenuFixture.메뉴상품(product)));
        final Menu menu = MenuFixture.메뉴(menuGroup, menuProducts);
        menu.setName(name);

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 메뉴상품은 한 가지 이상이어야한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void create_MenuProduct(List menuProducts) {
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.메뉴그룹());
        final Menu menu = MenuFixture.메뉴(menuGroup, menuProducts);

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 메뉴상품은 등록된 상품이어야한다.")
    @Test
    void create_MenuProduct_without_Product() {
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.메뉴그룹());
        final Product product = ProductFixture.상품();
        final List<MenuProduct> menuProducts = new ArrayList<>(Arrays.asList(MenuFixture.메뉴상품(product)));
        final Menu menu = MenuFixture.메뉴(menuGroup, menuProducts);

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 메뉴상품 수량은 0보다 커야한다.")
    @ValueSource(strings = "-1")
    @ParameterizedTest
    void create_MenuProduct_quantity(int quantity) {
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.메뉴그룹());
        final Product product = productRepository.save(ProductFixture.상품());
        final MenuProduct menuProduct = MenuFixture.메뉴상품(product);
        menuProduct.setQuantity(quantity);
        final List<MenuProduct> menuProducts = new ArrayList<>(Arrays.asList(menuProduct));
        final Menu menu = MenuFixture.메뉴(menuGroup, menuProducts);

        assertThatThrownBy(() -> 메뉴등록(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.메뉴그룹());
        final Product product = productRepository.save(ProductFixture.상품());
        final List<MenuProduct> menuProducts = new ArrayList<>(Arrays.asList(MenuFixture.메뉴상품(product)));
        final Menu saved = menuRepository.save(MenuFixture.메뉴(menuGroup, menuProducts));
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
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.메뉴그룹());
        final Product product = productRepository.save(ProductFixture.상품());
        final List<MenuProduct> menuProducts = new ArrayList<>(Arrays.asList(MenuFixture.메뉴상품(product)));
        final Menu saved = menuRepository.save(MenuFixture.메뉴(menuGroup, menuProducts));
        final Menu request = new Menu();
        request.setPrice(price);

        assertThatThrownBy(() -> 메뉴가격변경(saved.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 노출할 수 있다.")
    @Test
    void display() {
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.메뉴그룹());
        final Product product = productRepository.save(ProductFixture.상품());
        final List<MenuProduct> menuProducts = new ArrayList<>(Arrays.asList(MenuFixture.메뉴상품(product)));
        final Menu menu = MenuFixture.메뉴(menuGroup, menuProducts);
        menu.setDisplayed(false);
        final Menu saved = menuRepository.save(menu);
        assertThat(saved.isDisplayed()).isFalse();

        final Menu expected = 메뉴노출(saved.getId());
        assertThat(expected.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 노출할땐 가격이 메뉴상품의 가격합보다 커서는 안된다.")
    @Test
    void display_price() {
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.메뉴그룹());
        final Product product = productRepository.save(ProductFixture.상품());
        final List<MenuProduct> menuProducts = new ArrayList<>(Arrays.asList(MenuFixture.메뉴상품(product)));
        final Menu menu = MenuFixture.메뉴(menuGroup, menuProducts);
        menu.setDisplayed(false);
        menu.setPrice(BigDecimal.valueOf(12_000L));
        final Menu saved = menuRepository.save(menu);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> 메뉴노출(saved.getId()));
    }

    @DisplayName("메뉴를 숨길 수 있다.")
    @Test
    void hide() {
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.메뉴그룹());
        final Product product = productRepository.save(ProductFixture.상품());
        final List<MenuProduct> menuProducts = new ArrayList<>(Arrays.asList(MenuFixture.메뉴상품(product)));
        final Menu saved = menuRepository.save(MenuFixture.메뉴(menuGroup, menuProducts));

        final Menu expected = menuService.hide(saved.getId());
        assertThat(expected.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 전체조회할 수 있다.")
    @Test
    void findAll(){
        final MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.메뉴그룹());
        final Product product = productRepository.save(ProductFixture.상품());
        final List<MenuProduct> menuProducts = new ArrayList<>(Arrays.asList(MenuFixture.메뉴상품(product)));
        final Menu saved1 = menuRepository.save(MenuFixture.메뉴(menuGroup, menuProducts));
        final Menu saved2 = menuRepository.save(MenuFixture.메뉴(menuGroup, menuProducts));

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
}
