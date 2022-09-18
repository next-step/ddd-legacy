package kitchenpos;

import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.factory.MenuGroupFactory;
import kitchenpos.factory.MenuProductFactory;
import kitchenpos.factory.ProductFactory;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private ProfanityClient profanityClient;

    @InjectMocks
    private MenuService menuService;

    @BeforeEach
    public void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        profanityClient = new FakeProfanityClient();
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    public void create() {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = productRepository.save(ProductFactory.of("황금올리브", 20000L));
        Product 호가든 = productRepository.save(ProductFactory.of("호가든", 5000L));

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든));

        Menu request = new Menu();
        request.setMenuGroupId(createMenuGroup.getId());
        request.setName("치맥세트");
        request.setPrice(BigDecimal.valueOf(22000L));
        request.setMenuProducts(menuProducts);

        final Menu actual = menuService.create(request);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("치맥세트");
        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(22000L));
        assertThat(actual.isDisplayed()).isEqualTo(false);
        assertThat(actual.getMenuProducts()).hasSize(2);
    }

    @DisplayName("메뉴는 특정 메뉴 그룹에 속해있다.")
    @Test
    public void create_menu_in_menu_group() {
        Product 황금올리브 = productRepository.save(ProductFactory.of("황금올리브", 20000L));
        Product 호가든 = productRepository.save(ProductFactory.of("호가든", 5000L));

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든));

        Menu request = new Menu();
        request.setMenuGroupId(null);
        request.setName("치맥세트");
        request.setPrice(BigDecimal.valueOf(22000L));
        request.setMenuProducts(menuProducts);

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @ParameterizedTest(name = "메뉴 등록 시, 가격은 필수로 입력되어야 하며 0원 이상이어야 한다.")
    @NullSource
    @ValueSource(strings = "-1")
    public void create_input_null_and_negative(BigDecimal price) {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = productRepository.save(ProductFactory.of("황금올리브", 20000L));
        Product 호가든 = productRepository.save(ProductFactory.of("호가든", 5000L));

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든));

        Menu request = new Menu();
        request.setMenuGroupId(createMenuGroup.getId());
        request.setName("치맥세트");
        request.setPrice(price);
        request.setMenuProducts(menuProducts);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @ParameterizedTest(name = "메뉴 등록 시, 이름은 필수로 입력되 비속어가 포함되어있으면 안된다.")
    @NullSource
    @ValueSource(strings = {"욕설이 포함된 이름", "비속어가 포함된 이름"})
    public void create_input_null_and_profanity(String name) {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = productRepository.save(ProductFactory.of("황금올리브", 20000L));
        Product 호가든 = productRepository.save(ProductFactory.of("호가든", 5000L));

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든));

        Menu request = new Menu();
        request.setMenuGroupId(createMenuGroup.getId());
        request.setName(name);
        request.setPrice(BigDecimal.valueOf(22000L));
        request.setMenuProducts(menuProducts);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴 등록 시, 메뉴에 속한 상품들의 총 가격이 메뉴 가격 보다 더 비싸야한다.")
    @Test
    public void create_products_expensive_then_price() {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = productRepository.save(ProductFactory.of("황금올리브", 20000L));
        Product 호가든 = productRepository.save(ProductFactory.of("호가든", 5000L));

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든,2));

        Menu request = new Menu();
        request.setMenuGroupId(createMenuGroup.getId());
        request.setName("치맥세트");
        request.setPrice(BigDecimal.valueOf(31000L));
        request.setMenuProducts(menuProducts);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @ParameterizedTest(name = "메뉴 등록 시, 메뉴에 속한 상품의 갯수가 음수일 수 없다.")
    @ValueSource(strings = "-1")
    public void create_negative_product_quantity(int quantity) {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = productRepository.save(ProductFactory.of("황금올리브", 20000L));
        Product 호가든 = productRepository.save(ProductFactory.of("호가든", 5000L));

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든,quantity));

        Menu request = new Menu();
        request.setMenuGroupId(createMenuGroup.getId());
        request.setName("치맥세트");
        request.setPrice(BigDecimal.valueOf(28000L));
        request.setMenuProducts(menuProducts);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴 등록 시, 등록되어있는 상품만 메뉴 등록 가능하다.")
    @Test
    public void create_exist_product() {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = ProductFactory.of("황금올리브", 20000L);
        Product 호가든 = ProductFactory.of("호가든", 5000L);

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든,2));

        Menu request = new Menu();
        request.setMenuGroupId(createMenuGroup.getId());
        request.setName("치맥세트");
        request.setPrice(BigDecimal.valueOf(28000L));
        request.setMenuProducts(menuProducts);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    /*
    - [ ] 메뉴의 가격을 수정한다.
    - [ ] 수정할 가격은 필수로 입력되어야 한다.
    - [ ] 메뉴에 속한 상품들의 총 가격보다 변경하려는 메뉴의 가격이 더 비싸다면 수정이 불가능하다
     */

    @DisplayName("메뉴 가격을 수정 할 수 있다.")
    @Test
    public void update() {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = ProductFactory.of("황금올리브", 20000L);
        Product 호가든 = ProductFactory.of("호가든", 5000L);

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든,2));

        final Menu menu = new Menu();
        menu.setMenuGroupId(createMenuGroup.getId());
        menu.setName("치맥세트");
        menu.setPrice(BigDecimal.valueOf(28000L));
        menu.setMenuProducts(menuProducts);

        final Menu request = menuRepository.save(menu);
        request.setPrice(BigDecimal.valueOf(27000L));

        Menu actual = menuService.changePrice(request.getId(), request);

        assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(27000L));
    }

    @ParameterizedTest(name = "메뉴 가격 수정 시, 가격은 필수로 입력되어야 한다.")
    @NullSource
    public void update_input_null(BigDecimal price) {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = ProductFactory.of("황금올리브", 20000L);
        Product 호가든 = ProductFactory.of("호가든", 5000L);

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든,2));

        final Menu menu = new Menu();
        menu.setMenuGroupId(createMenuGroup.getId());
        menu.setName("치맥세트");
        menu.setPrice(BigDecimal.valueOf(28000L));
        menu.setMenuProducts(menuProducts);

        final Menu request = menuRepository.save(menu);
        request.setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(request.getId(), request));
    }

    @DisplayName("메뉴 가격 수정 시, 메뉴에 속한 상품들의 총 가격보다 변경하려는 메뉴의 가격이 더 비싸다면 수정이 불가능하다.")
    @Test
    public void update_products_expensive_then_price() {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = ProductFactory.of("황금올리브", 20000L);
        Product 호가든 = ProductFactory.of("호가든", 5000L);

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든,2));

        final Menu menu = new Menu();
        menu.setMenuGroupId(createMenuGroup.getId());
        menu.setName("치맥세트");
        menu.setPrice(BigDecimal.valueOf(28000L));
        menu.setMenuProducts(menuProducts);

        final Menu request = menuRepository.save(menu);
        request.setPrice(BigDecimal.valueOf(31000L));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(request.getId(), request));
    }

    @DisplayName("메뉴는 진열이 가능하다.")
    @Test
    public void display() {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = ProductFactory.of("황금올리브", 20000L);
        Product 호가든 = ProductFactory.of("호가든", 5000L);

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든,2));

        final Menu menu = new Menu();
        menu.setMenuGroupId(createMenuGroup.getId());
        menu.setName("치맥세트");
        menu.setPrice(BigDecimal.valueOf(28000L));
        menu.setMenuProducts(menuProducts);

        final Menu request = menuRepository.save(menu);

        Menu actual = menuService.display(request.getId());

        assertThat(actual.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴에 속한 상품들의 총 가격보다 메뉴의 가격이 더 비싸다면 진열이 불가능하다.")
    @Test
    public void display_products_expensive_then_price() {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = ProductFactory.of("황금올리브", 20000L);
        Product 호가든 = ProductFactory.of("호가든", 5000L);

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든,2));

        final Menu menu = new Menu();
        menu.setMenuGroupId(createMenuGroup.getId());
        menu.setName("치맥세트");
        menu.setPrice(BigDecimal.valueOf(31000L));
        menu.setMenuProducts(menuProducts);

        Menu request = menuRepository.save(menu);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> menuService.display(request.getId()));
    }

    @DisplayName("메뉴를 숨길 수 있다.")
    @Test
    public void hide() {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = ProductFactory.of("황금올리브", 20000L);
        Product 호가든 = ProductFactory.of("호가든", 5000L);

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든,2));

        final Menu menu = new Menu();
        menu.setMenuGroupId(createMenuGroup.getId());
        menu.setName("치맥세트");
        menu.setPrice(BigDecimal.valueOf(28000L));
        menu.setMenuProducts(menuProducts);

        Menu request = menuRepository.save(menu);

        Menu actual = menuService.hide(request.getId());

        assertThat(actual.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 조회할 수 있다.")
    @Test
    public void findAll() {
        MenuGroup menuGroup = MenuGroupFactory.getDefaultMenuGroup();
        MenuGroup createMenuGroup = menuGroupRepository.save(menuGroup);

        Product 황금올리브 = ProductFactory.of("황금올리브", 20000L);
        Product 호가든 = ProductFactory.of("호가든", 5000L);

        List<MenuProduct> menuProducts = List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든,2));

        final Menu menu = new Menu();
        menu.setMenuGroupId(createMenuGroup.getId());
        menu.setName("치맥세트");
        menu.setPrice(BigDecimal.valueOf(28000L));
        menu.setMenuProducts(menuProducts);

        Menu request = menuRepository.save(menu);

        List<Menu> menus = menuService.findAll();

        assertThat(menus).hasSize(1);
    }

}
