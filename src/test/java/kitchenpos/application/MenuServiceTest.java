package kitchenpos.application;


import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fake.FakePugomalumClinet;
import kitchenpos.fake.InMemoryMenuGroupRepository;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fake.InMemoryProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.web.client.RestTemplateBuilder;

@DisplayName("메뉴 테스트")
class MenuServiceTest {

    private MenuService menuService;

    private MenuRepository menuRepository;

    private MenuGroup 등록된_메뉴그룹;

    private Product 등록된_상품;

    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        ProductRepository productRepository = new InMemoryProductRepository();
        MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
        PurgomalumClient purgomalumClient = new FakePugomalumClinet(new RestTemplateBuilder());
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

        등록된_메뉴그룹 = menuGroupRepository.save(createMenuGroup("세트1"));
        등록된_상품 = productRepository.save(createProduct());
    }

    @DisplayName("메뉴의 가격은 0원 이상 이어야 한다.")
    @ParameterizedTest
    @MethodSource("bigDecimalZeroAndNull")
    void price_is_less_then_zero(BigDecimal price) {
        Menu menu = createMenu(price);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(menu)
                );
    }

    @DisplayName("메뉴의 등록된 메뉴 그룹이 존재 해야 한다.")
    @Test
    void menu_has_menuGroup() {
        Menu request = createMenu();

        request.setMenuGroup(createMenuGroup("세트1"));

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(request)
                );
    }

    @DisplayName("메뉴의 상품은 하나 이상 필요하다.")
    @Test
    void menu_in_menuProduct() {
        Menu request = createMenu(등록된_메뉴그룹);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));

    }

    @DisplayName("메뉴의 상품은 등록 되어 있는 상품 이어야 한다.")
    @Test
    void menu_in_saved_product() {
        // given
        Menu request = createMenu(등록된_메뉴그룹);
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(createProduct());

        // when
        request.setMenuProducts(List.of(menuProduct));

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴의 상품은 수량이 0보다 작으면 안된다.")
    @Test
    void meunProduct_quantity_no_less_than_zero() {
        // given
        Menu request = createMenu(등록된_메뉴그룹);
        MenuProduct menuProduct = createMenuProduct(createProduct(), -1);
        request.setMenuProducts(List.of(menuProduct));

        // when & then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴의 가격이 상품의 합계 보다 크면 안된다.")
    @Test
    void menuPrice_less_then_product_sum() {
        //given
        Menu request = createMenu(등록된_메뉴그룹);
        MenuProduct menuProduct = createMenuProduct(등록된_상품, 1);
        request.setPrice(BigDecimal.valueOf(2000));

        request.setMenuProducts(List.of(menuProduct));

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));
    }

    @DisplayName("메뉴이름에는 비속어가 아니어야 한다.")
    @Test
    void menu_name_is_no_purgomalum() {
        Menu request = createMenu(등록된_메뉴그룹, "비속어");

        MenuProduct menuProduct = createMenuProduct(등록된_상품, 3);
        request.setMenuProducts(List.of(menuProduct));

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(request));

    }

    @DisplayName("메뉴가 생성된다")
    @Test
    void create() {
        //given
        Menu request = createMenu(등록된_메뉴그룹);
        request.setPrice(BigDecimal.valueOf(2_000));

        MenuProduct menuProduct = createMenuProduct(등록된_상품, 3);
        request.setMenuProducts(List.of(menuProduct));

        //when
        Menu createMenu = menuService.create(request);

        //then
        assertAll(
                () -> assertThat(createMenu.getName()).isEqualTo(request.getName()),
                () -> assertThat(createMenu.getId()).isNotNull()
        );
    }

    @ParameterizedTest
    @DisplayName("변경할 메뉴의 가격이 있어야 한다.")
    @MethodSource("bigDecimalZeroAndNull")
    void changePrice_has_request_menu_price(BigDecimal price) {
        Menu request = createMenu(price);

        assertThatIllegalArgumentException().isThrownBy(() ->
                menuService.changePrice(UUID.randomUUID(), request)
        );
    }

    @Test
    @DisplayName("메뉴가 존재 해야 변경이 가능하다.")
    void changePrice_has_request_menu() {
        Menu request = createMenu(BigDecimal.valueOf(3000));

        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                menuService.changePrice(UUID.randomUUID(), request)
        );
    }

    @Test
    @DisplayName("메뉴의 가격은 상품의 합보다 작아야 한다.")
    void change_menu_price_is_lessThen_productSum() {
        Menu createMenu = 등록된_메뉴(등록된_메뉴그룹, BigDecimal.valueOf(2_000), List.of(createMenuProduct(등록된_상품, 3)));
        Menu request = createMenu(BigDecimal.valueOf(4_000));

        assertThatIllegalArgumentException().isThrownBy(() ->
                menuService.changePrice(createMenu.getId(), request)
        );
    }

    @Test
    @DisplayName("메뉴의 가격이 변경 된다.")
    void changePrice() {
        Menu createMenu = 등록된_메뉴(등록된_메뉴그룹, BigDecimal.valueOf(2_000), List.of(createMenuProduct(등록된_상품, 3)));
        Menu request = createMenu(BigDecimal.valueOf(1_000));

        final Menu changePriceMenu = menuService.changePrice(createMenu.getId(), request);

        assertThat(changePriceMenu.getPrice()).isEqualTo(BigDecimal.valueOf(1_000));
    }

    @Test
    @DisplayName("등록된 메뉴가 없으면 표시할 수 없다.")
    void noMenuIsNot() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.display(UUID.randomUUID()));
    }

    @Test
    @DisplayName("등록된 메뉴의 가격은 상품과 금액 * 수량의 합계보다 크면 표시할 수 없다.")
    void productSum_lessThen_menuPrice_no_display() {
        Menu menu = createMenu(등록된_메뉴그룹);
        menu.setPrice(BigDecimal.valueOf(4_000));
        menu.setMenuProducts(List.of(createMenuProduct(등록된_상품, 3)));

        final Menu saveMenu = menuRepository.save(menu);

        assertThatIllegalArgumentException().isThrownBy(()->
                menuService.display(saveMenu.getId())
        );
    }

    @Test
    @DisplayName("등록된 메뉴를 표시 한다.")
    void display(){
        Menu createMenu = 등록된_메뉴(등록된_메뉴그룹, BigDecimal.valueOf(2_000), List.of(createMenuProduct(등록된_상품, 3)));

        Menu displayMenu = menuService.display(createMenu.getId());

        assertThat(displayMenu.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("등록된 메뉴가 없으면 숨길수 없다.")
    void noMenuIsNoHide() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.hide(UUID.randomUUID()));
    }

    @Test
    @DisplayName("메뉴를 숨긴다.")
    void hide() {
        // given
        Menu menu = createMenu(등록된_메뉴그룹);
        menu.setPrice(BigDecimal.valueOf(2_000));
        menu.setMenuProducts(List.of(createMenuProduct(등록된_상품, 3)));
        menu.setDisplayed(true);
        final Menu saveMenu = menuRepository.save(menu);

        //when
        final Menu hideMenu = menuService.hide(saveMenu.getId());

        //then
        assertThat(hideMenu.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("등록된 메뉴를 조회 한다.")
    void findAll() {
        Menu createMenu1 = 등록된_메뉴(등록된_메뉴그룹, BigDecimal.valueOf(2_000), List.of(createMenuProduct(등록된_상품, 3)));
        Menu createMenu2 = 등록된_메뉴(등록된_메뉴그룹, BigDecimal.valueOf(3_000), List.of(createMenuProduct(등록된_상품, 6)));

        final List<Menu> menus = menuService.findAll();

        assertThat(menus).extracting("id")
                .containsExactly(createMenu1.getId(), createMenu2.getId());
    }


    private Menu 등록된_메뉴(MenuGroup menuGroup, BigDecimal price, List<MenuProduct> menuProducts) {
        Menu request = createMenu(menuGroup);
        request.setPrice(price);
        request.setMenuProducts(menuProducts);
        return menuService.create(request);
    }




    private static Stream<BigDecimal> bigDecimalZeroAndNull() {
        return Stream.of(null, new BigDecimal(-1));
    }

}
