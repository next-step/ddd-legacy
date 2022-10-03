package kitchenpos.application;

import factory.MenuFactory;
import factory.MenuGroupFactory;
import factory.MenuProductFactory;
import factory.ProductFactory;
import kitchenpos.domain.*;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    private static final int DEFAULT_QUANTITY = 2;
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private ProfanityClient profanityClient;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        profanityClient = new FakeProfanityClient();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void create() {
        final Menu request = createMenu(createMenuProduct());

        final Menu actual = menuService.create(request);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("치맥세트");
        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(28000L));
        assertThat(actual.isDisplayed()).isFalse();
        assertThat(actual.getMenuProducts()).hasSize(2);
    }

    @DisplayName("메뉴는 특정 메뉴 그룹에 속해있다.")
    @Test
    void create_menu_in_menu_group() {
        final List<MenuProduct> menuProducts = createMenuProduct();

        final Menu request = MenuFactory.of(menuProducts);

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @ParameterizedTest(name = "메뉴 등록 시, 가격은 필수로 입력되어야 하며 0원 이상이어야 한다.")
    @NullSource
    @ValueSource(strings = "-1000")
    void create_input_null_and_negative(BigDecimal price) {
        final MenuGroup createMenuGroup = createMenuGroup();
        final List<MenuProduct> menuProducts = createMenuProduct();

        final Menu request = MenuFactory.of(createMenuGroup, menuProducts, price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @ParameterizedTest(name = "메뉴 등록 시, 이름은 필수로 입력되 비속어가 포함되어있으면 안된다.")
    @NullSource
    @ValueSource(strings = {"욕설이 포함된 이름", "비속어가 포함된 이름"})
    void create_input_null_and_profanity(String name) {
        final MenuGroup createMenuGroup = createMenuGroup();
        final List<MenuProduct> menuProducts = createMenuProduct();

        final Menu request = MenuFactory.of(createMenuGroup, menuProducts, name);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @ParameterizedTest(name = "메뉴 등록 시, 메뉴에 속한 상품들의 총 가격이 메뉴 가격 보다 더 비싸야한다.")
    @ValueSource(strings = "100000")
    void create_products_expensive_then_price(long price) {
        final MenuGroup createMenuGroup = createMenuGroup();
        final List<MenuProduct> menuProducts = createMenuProduct();
        final Menu menu = MenuFactory.of(createMenuGroup, menuProducts, price);

        final Menu request = menuRepository.save(menu);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @ParameterizedTest(name = "메뉴 등록 시, 메뉴에 속한 상품의 갯수가 음수일 수 없다.")
    @ValueSource(strings = "-1")
    void create_negative_product_quantity(int quantity) {
        final Menu request = createMenu(createMenuProduct(quantity));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴 등록 시, 등록되어있는 상품만 메뉴 등록 가능하다.")
    @Test
    void create_exist_product() {
        final MenuGroup createMenuGroup = createMenuGroup();
        final Menu request = MenuFactory.of(createMenuGroup);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴 가격을 수정 할 수 있다.")
    @Test
    void update() {
        final Menu request = createMenuAndSave();
        request.setPrice(BigDecimal.valueOf(27000L));

        Menu actual = menuService.changePrice(request.getId(), request);

        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(27000L));
    }

    @ParameterizedTest(name = "메뉴 가격 수정 시, 가격은 필수로 입력되어야 한다.")
    @NullSource
    void update_input_null(BigDecimal price) {
        final Menu request = createMenuAndSave();
        request.setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(request.getId(), request));
    }

    @ParameterizedTest(name = "메뉴 가격 수정 시, 메뉴에 속한 상품들의 총 가격보다 변경하려는 메뉴의 가격이 더 비싸다면 수정이 불가능하다.")
    @ValueSource(strings = "31000")
    void update_products_expensive_then_price(long price) {
        final MenuGroup createMenuGroup = createMenuGroup();
        final Menu menu = MenuFactory.of(createMenuGroup, price);

        final Menu request = menuRepository.save(menu);
        request.setPrice(BigDecimal.valueOf(price));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(request.getId(), request));
    }

    @DisplayName("메뉴는 진열이 가능하다.")
    @Test
    void display() {
        final Menu request = createMenuAndSave();

        final Menu actual = menuService.display(request.getId());

        assertThat(actual.isDisplayed()).isTrue();
    }

    @ParameterizedTest(name = "메뉴에 속한 상품들의 총 가격보다 메뉴의 가격이 더 비싸다면 진열이 불가능하다.")
    @ValueSource(strings = "100000")
    void display_products_expensive_then_price(long price) {
        final Menu request = createMenuAndSave();
        request.setPrice(BigDecimal.valueOf(price));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> menuService.display(request.getId()));
    }

    @DisplayName("메뉴를 숨길 수 있다.")
    @Test
    void hide() {
        final Menu request = createMenuAndSave();

        final Menu actual = menuService.hide(request.getId());

        assertThat(actual.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 조회할 수 있다.")
    @Test
    void findAll() {
        final Menu menu = createMenu(createMenuProduct());
        menuRepository.save(menu);

        List<Menu> menus = menuService.findAll();

        assertThat(menus).hasSize(1);
    }

    private MenuGroup createMenuGroup() {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);
        return createMenuGroup;
    }

    private List<MenuProduct> createMenuProduct() {
        return createMenuProduct(DEFAULT_QUANTITY);
    }

    private List<MenuProduct> createMenuProduct(int quantity) {
        Product 황금올리브 = productRepository.save(ProductFactory.of("황금올리브", 20000L));
        Product 호가든 = productRepository.save(ProductFactory.of("호가든", 5000L));
        return List.of(MenuProductFactory.of(황금올리브, quantity), MenuProductFactory.of(호가든, quantity));
    }

    private Menu createMenuAndSave() {
        final Menu menu = createMenu(createMenuProduct());
        return menuRepository.save(menu);
    }

    private Menu createMenu(List<MenuProduct> MenuProduct) {
        final MenuGroup createMenuGroup = createMenuGroup();
        final List<MenuProduct> menuProducts = MenuProduct;
        final Menu request = MenuFactory.getDefaultMenu(createMenuGroup, menuProducts);
        return request;
    }
}
