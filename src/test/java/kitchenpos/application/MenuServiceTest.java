package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    @Mock
    private MenuGroupRepository menuGroupRepository;

    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(
            menuRepository,
            menuGroupRepository,
            productRepository,
            purgomalumClient
        );
    }

    @DisplayName("메뉴를 화면에서 숨길 수 있다.")
    @Test
    void hideMenu() {
        // given
        Product product = ProductFixture.createProduct();

        MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product);

        Menu menu = MenuFixture.createMenu();
        MenuProductFixture.createMenuProduct(product);
        menu.setMenuProducts(List.of(menuProduct));

        menuRepository.save(menu);

        // when
        Menu result = menuService.hide(menu.getId());

        // then
        assertAll(
            () -> assertThat(result).isNotNull(),
            () -> assertThat(result.isDisplayed()).isFalse()
        );
    }

    @DisplayName("메뉴를 조회할 수 있다.")
    @Test
    void findAllMenus() {
        // given
        // when
        List<Menu> menus = menuService.findAll();

        // then
        assertAll(
            () -> assertThat(menus).isNotNull(),
            () -> assertThat(menus).isEmpty()
        );
    }

    @Nested
    class CreateMenuTests {

        @DisplayName("`메뉴`를 생성할 수 있다.")
        @Test
        void createMenuWithValidInput() {
            // given
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());

            Product product = ProductFixture.createProduct();

            MenuProduct menuProductRequest = MenuProductFixture.createMenuProduct(product);

            Menu validRequest = new Menu();
            validRequest.setName("메뉴 이름");
            validRequest.setPrice(product.getPrice());
            validRequest.setMenuGroupId(menuGroup.getId());
            validRequest.setMenuProducts(List.of(menuProductRequest));
            validRequest.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
            productRepository.save(product);

            // when
            Menu result = menuService.create(validRequest);

            // then
            assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo(validRequest.getName()),
                () -> assertThat(result.getPrice()).isEqualTo(validRequest.getPrice()),
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getMenuGroup()).isEqualTo(menuGroup),
                () -> assertThat(result.getMenuProducts()).hasSize(1),
                () -> assertThat(result.isDisplayed()).isTrue()
            );
        }

        @Test
        void createMenuWithNegativePrice() {
            // given
            Menu request = new Menu();
            request.setName("Valid Menu");
            request.setPrice(BigDecimal.valueOf(-1000));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
        }

        @Test
        void createMenuWithNullPrice() {
            Menu request = new Menu();
            request.setName("Valid Menu");
            request.setPrice(null);

            assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
        }

        @Test
        void createMenuWithNullName() {
            // given
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());

            Product product = ProductFixture.createProduct();

            MenuProduct menuProductRequest = MenuProductFixture.createMenuProduct(product);

            Menu invalidMenuRequest = new Menu();
            invalidMenuRequest.setName(null);
            invalidMenuRequest.setPrice(product.getPrice());
            invalidMenuRequest.setMenuGroupId(menuGroup.getId());
            invalidMenuRequest.setMenuProducts(List.of(menuProductRequest));
            invalidMenuRequest.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> menuService.create(invalidMenuRequest));
        }

        @Test
        void createMenuWithProfanityInName() {
            // given
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());

            Product product = ProductFixture.createProduct();

            MenuProduct menuProductRequest = MenuProductFixture.createMenuProduct(product);

            Menu request = new Menu();
            request.setName("대충 나쁜 말");
            request.setPrice(product.getPrice());
            request.setMenuGroupId(menuGroup.getId());
            request.setMenuProducts(List.of(menuProductRequest));
            request.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
        }

        @DisplayName("지정한 '메뉴 그룹'이 존재하지 않으면 생성할 수 없다.")
        @Test
        void createMenuWithEmptyMenuProducts() {
            // given
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());

            Menu request = new Menu();
            request.setName("메뉴 이름");
            request.setPrice(BigDecimal.valueOf(1000));
            request.setMenuGroupId(menuGroup.getId());
            request.setMenuProducts(List.of());
            request.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
        }

        @DisplayName("지정한 '메뉴 그룹'이 존재하지 않으면 생성할 수 없다.")
        @Test
        void createMenuWithNullMenuProducts() {
            // given
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());

            Menu request = new Menu();
            request.setName("메뉴 이름");
            request.setPrice(BigDecimal.valueOf(1000));
            request.setMenuGroupId(menuGroup.getId());
            request.setMenuProducts(null);
            request.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
        }

        @DisplayName("지정한 '메뉴 상품' 중 하나라도 '상품'이 존재하지 않으면 생성할 수 없다.")
        @Test
        void createMenuWithInvalidMenuProduct() {
            // given
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());

            Product product = ProductFixture.createProduct();

            MenuProduct menuProductRequest = MenuProductFixture.createMenuProduct(product);

            Menu request = new Menu();
            request.setName("메뉴 이름");
            request.setPrice(product.getPrice());
            request.setMenuGroupId(menuGroup.getId());
            request.setMenuProducts(List.of(menuProductRequest));
            request.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
        }

        @DisplayName("지정한 '메뉴 상품' 중 수량이 음수인 경우 생성할 수 없다.")
        @Test
        void createMenuWithNegativeQuantity() {
            // given
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());

            Product product = ProductFixture.createProduct();

            MenuProduct menuProductRequest = MenuProductFixture.createMenuProduct(product, -1L);

            Menu request = new Menu();
            request.setName("메뉴 이름");
            request.setPrice(product.getPrice());
            request.setMenuGroupId(menuGroup.getId());
            request.setMenuProducts(List.of(menuProductRequest));
            request.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
        }

        @DisplayName("메뉴의 가격의 합이 메뉴의 가격보다 작은 경우 생성할 수 없다.")
        @Test
        void createMenuWithInvalidPrice() {
            // given
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());

            Product product = ProductFixture.createProduct();

            MenuProduct menuProductRequest = MenuProductFixture.createMenuProduct(product);

            Menu request = new Menu();
            request.setName("메뉴 이름");
            request.setPrice(product.getPrice().add(BigDecimal.valueOf(1000)));
            request.setMenuGroupId(menuGroup.getId());
            request.setMenuProducts(List.of(menuProductRequest));
            request.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
        }
    }

    @Nested
    class DisplayMenu {

        @DisplayName("메뉴를 화면에 표시할 수 있다.")
        @Test
        void displayMenu() {
            // give
            Product product = ProductFixture.createProduct();

            MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product);

            Menu menu = MenuFixture.createMenu(false, List.of(menuProduct));

            menuRepository.save(menu);

            // when
            Menu result = menuService.display(menu.getId());

            // then
            assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.isDisplayed()).isTrue()
            );
        }

        @DisplayName("`메뉴` 노출 시, `메뉴`의 가격이 `메뉴 상품`들의 가격 총합을 초과하면 안된다.")
        @Test
        void displayMenuWithInvalidPrice() {
            // given
            Product product = ProductFixture.createProduct();

            MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product);

            Menu menu = MenuFixture.createMenu(
                product.getPrice().add(BigDecimal.valueOf(1000)),
                List.of(menuProduct)
            );

            menuRepository.save(menu);

            // when & then
            assertThrows(IllegalStateException.class, () -> menuService.display(menu.getId()));
        }
    }

    @Nested
    class ChangePriceTests {

        @DisplayName("메뉴의 가격을 변경할 수 있다.")
        @Test
        void changePriceWithValidInput() {
            // given
            Product product = ProductFixture.createProduct();

            MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product);

            Menu menu = MenuFixture.createMenu(List.of(menuProduct));

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(2000));

            menuRepository.save(menu);
            productRepository.save(product);

            // when
            Menu result = menuService.changePrice(menu.getId(), request);

            // then
            assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getPrice()).isEqualTo(request.getPrice())
            );
        }

        @DisplayName("메뉴의 가격이 null이면 변경할 수 없다.")
        @Test
        void changePriceWithNullPrice() {
            // given
            Menu request = new Menu();
            request.setPrice(null);

            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> menuService.changePrice(UUID.randomUUID(), request));
        }

        @DisplayName("메뉴의 가격이 음수이면 변경할 수 없다.")
        @Test
        void changePriceWithNegativePrice() {
            // given
            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(-1000));

            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> menuService.changePrice(UUID.randomUUID(), request));
        }

        @DisplayName("`메뉴`의 가격 변경 시, 변경된 가격은 `메뉴 상품`들의 가격 총합을 초과할 수 없다.")
        @Test
        void changePriceWithInvalidPrice() {
            // given
            Product product = ProductFixture.createProduct();

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            Menu menu = MenuFixture.createMenu(
                product.getPrice().add(BigDecimal.valueOf(1000)),
                List.of(menuProduct)
            );

            menuRepository.save(menu);
            productRepository.save(product);

            Menu request = new Menu();
            request.setPrice(product.getPrice().add(BigDecimal.valueOf(1000)));

            // when & then
            assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

}
