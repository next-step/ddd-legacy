package kitchenpos.application;

import static kitchenpos.builder.TestFixtureFactory.createMenuWithProductAndGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.builder.TestFixtureFactory;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class MenuServiceTest {

    private MenuService menuService;
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        menuRepository = mock(MenuRepository.class);
        menuGroupRepository = mock(MenuGroupRepository.class);
        productRepository = mock(ProductRepository.class);
        purgomalumClient = mock(PurgomalumClient.class);
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @Test
    @DisplayName("메뉴를 생성한다")
    void create_menu() {
        // given
        MenuGroup menuGroup = createMenuGroup();
        Product product = createProduct(BigDecimal.valueOf(5000));
        Menu request = createMenuRequest("김치찌개", 8000, menuGroup, product);

        when(menuGroupRepository.findById(any(UUID.class))).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Menu created = menuService.create(request);

        // then
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("김치찌개");
        assertThat(created.getPrice()).isEqualTo(BigDecimal.valueOf(8000));
        assertThat(created.getMenuGroup()).isEqualTo(menuGroup);
        verify(menuRepository).save(any(Menu.class));
    }

    @Test
    @DisplayName("메뉴 가격은 0원 미만이면 예외가 발생한다.")
    void menu_price_exception() {
        // given
        MenuGroup menuGroup = createMenuGroup();
        Product product = createProduct(BigDecimal.valueOf(5000));
        Menu request = createMenuRequest("김치찌개", -1000, menuGroup,
                product);

        when(menuGroupRepository.findById(any(UUID.class))).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("메뉴에 이름이 없으면 예외가 발생한다.")
    void menu_name_exists_exception(String name) {
        // given
        MenuGroup menuGroup = createMenuGroup();
        Product product = createProduct(BigDecimal.valueOf(5000));
        Menu request = createMenuRequest(name, 8000, menuGroup,
                product);

        when(menuGroupRepository.findById(any(UUID.class))).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 이름에 비속어가 포함되면 예외가 발생한다")
    void menu_name_profanity_exception() {
        // given
        MenuGroup menuGroup = createMenuGroup();
        Product product = createProduct(BigDecimal.valueOf(5000));
        Menu request = createMenuRequest("fuck", 8000, menuGroup,
                product);

        when(menuGroupRepository.findById(any(UUID.class))).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(purgomalumClient.containsProfanity(any())).thenReturn(true);
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 그룹이 존재하지 않으면 예외가 발생한다")
    void menu_group_exception() {
        // given
        Product product = createProduct(BigDecimal.valueOf(5000));
        MenuGroup menuGroup = null;
        MenuProduct menuProduct = new MenuProduct(1, product, product.getId());
        Menu request = new Menu("김치찌개", BigDecimal.valueOf(8000), true, List.of(menuProduct), menuGroup,
                null);

        when(menuGroupRepository.findById(any(UUID.class))).thenReturn(Optional.ofNullable(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("메뉴의 판매 가격이 재료 가격의 총합 낮으면 예외가 발생한다.")
    void create_menu_with_menuPrice_andTotalPrice_exception() {
        // given
        MenuGroup menuGroup = createMenuGroup();
        Product product = createProduct(BigDecimal.valueOf(10000));
        Menu request = createMenuRequest("김치찌개", 8000, menuGroup, product);
        request.setPrice(BigDecimal.valueOf(8000));

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        // when // then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 가격을 변경할 수 있다")
    void change_price() {
        // given
        Menu menu = TestFixtureFactory.createMenuWithProductAndGroup();
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(12000));

        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when
        Menu updated = menuService.changePrice(menu.getId(), request);

        // then
        assertThat(updated.getPrice()).isEqualTo(BigDecimal.valueOf(12000));
    }

    @Test
    @DisplayName("변경하려는 가격이 재료 가격의 총합보다 낮으면 예외가 발생한다")
    void change_price_with_menuPrice_andTotalPrice_exception() {
        // given
        Menu menu = TestFixtureFactory.createMenuWithProductAndGroup();
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(4000));

        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when // then
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴를 표시 상태로 변경할 수 있다")
    void display() {
        // given
        Menu menu = TestFixtureFactory.createMenuWithProductAndGroup();
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when
        Menu displayed = menuService.display(menu.getId());

        // then
        assertThat(displayed.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("메뉴를 숨김 상태로 변경할 수 있다")
    void hide() {
        // given
        Menu menu = TestFixtureFactory.createMenuWithProductAndGroup();
        menu.setDisplayed(true);
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when
        Menu hidden = menuService.hide(menu.getId());

        // then
        assertThat(hidden.isDisplayed()).isFalse();
    }


    @Test
    @DisplayName("전체 메뉴를 조회할 수 있다")
    void find_allMenus() {
        // given
        List<Menu> menus = List.of(
                TestFixtureFactory.createMenuWithProductAndGroup(), TestFixtureFactory.createMenuWithProductAndGroup());
        when(menuRepository.findAll()).thenReturn(menus);

        // when
        List<Menu> found = menuService.findAll();

        // then
        assertThat(found).hasSize(2);
    }

    private MenuGroup createMenuGroup() {
        return new MenuGroup("한식");
    }

    private Product createProduct(BigDecimal price) {
        return new Product("김치", price);
    }

    private Menu createMenuRequest(String name, int price, MenuGroup menuGroup, Product product) {
        MenuProduct menuProduct = new MenuProduct(1, product, product.getId());
        return new Menu(name, BigDecimal.valueOf(price), true, List.of(menuProduct), menuGroup,
                menuGroup.getId());
    }
}
