package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
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

    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

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
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(2000));

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);

        Menu menu = new Menu();
        menu.setMenuProducts(List.of(menuProduct));
        menu.setPrice(BigDecimal.valueOf(1500));
        menu.setDisplayed(true);

        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when
        Menu result = menuService.hide(UUID.randomUUID());

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
        when(menuRepository.findAll()).thenReturn(Collections.emptyList());

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

            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(BigDecimal.valueOf(1000));

            MenuProduct menuProductRequest = new MenuProduct();
            menuProductRequest.setProductId(product.getId());
            menuProductRequest.setQuantity(1);

            Menu validRequest = new Menu();
            validRequest.setName("메뉴 이름");
            validRequest.setPrice(BigDecimal.valueOf(1000));
            validRequest.setMenuGroupId(menuGroup.getId());
            validRequest.setMenuProducts(List.of(menuProductRequest));
            validRequest.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
            when(productRepository.findById(any())).thenReturn(Optional.of(product));
            when(purgomalumClient.containsProfanity(any())).thenReturn(false);
            when(menuRepository.save(any())).then(invocation -> invocation.getArgument(0));

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

            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(BigDecimal.valueOf(1000));

            MenuProduct menuProductRequest = new MenuProduct();
            menuProductRequest.setProductId(product.getId());
            menuProductRequest.setQuantity(1);

            Menu invalidMenuRequest = new Menu();
            invalidMenuRequest.setName(null);
            invalidMenuRequest.setPrice(BigDecimal.valueOf(1000));
            invalidMenuRequest.setMenuGroupId(menuGroup.getId());
            invalidMenuRequest.setMenuProducts(List.of(menuProductRequest));
            invalidMenuRequest.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
            when(productRepository.findById(any())).thenReturn(Optional.of(product));

            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> menuService.create(invalidMenuRequest));
        }

        @Test
        void createMenuWithProfanityInName() {
            // given
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());

            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(BigDecimal.valueOf(1000));

            MenuProduct menuProductRequest = new MenuProduct();
            menuProductRequest.setProductId(product.getId());
            menuProductRequest.setQuantity(1);

            Menu request = new Menu();
            request.setName("대충 나쁜 말");
            request.setPrice(BigDecimal.valueOf(1000));
            request.setMenuGroupId(menuGroup.getId());
            request.setMenuProducts(List.of(menuProductRequest));
            request.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
            when(productRepository.findById(any())).thenReturn(Optional.of(product));
            when(purgomalumClient.containsProfanity(any())).thenReturn(true);

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

            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(BigDecimal.valueOf(1000));

            MenuProduct menuProductRequest = new MenuProduct();
            menuProductRequest.setProductId(product.getId());
            menuProductRequest.setQuantity(1);

            Menu request = new Menu();
            request.setName("메뉴 이름");
            request.setPrice(BigDecimal.valueOf(1000));
            request.setMenuGroupId(menuGroup.getId());
            request.setMenuProducts(List.of(menuProductRequest));
            request.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
        }

        @DisplayName("지정한 '메뉴 상품' 중 수량이 음수인 경우 생성할 수 없다.")
        @Test
        void createMenuWithNegativeQuantity() {
            // given
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());

            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(BigDecimal.valueOf(1000));

            MenuProduct menuProductRequest = new MenuProduct();
            menuProductRequest.setProductId(product.getId());
            menuProductRequest.setQuantity(-1);

            Menu request = new Menu();
            request.setName("메뉴 이름");
            request.setPrice(BigDecimal.valueOf(1000));
            request.setMenuGroupId(menuGroup.getId());
            request.setMenuProducts(List.of(menuProductRequest));
            request.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
        }

        @DisplayName("메뉴의 가격의 합이 메뉴의 가격보다 작은 경우 생성할 수 없다.")
        @Test
        void createMenuWithInvalidPrice() {
            // given
            MenuGroup menuGroup = new MenuGroup();
            menuGroup.setId(UUID.randomUUID());

            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(BigDecimal.valueOf(1000));

            MenuProduct menuProductRequest = new MenuProduct();
            menuProductRequest.setProductId(product.getId());
            menuProductRequest.setQuantity(1);

            Menu request = new Menu();
            request.setName("메뉴 이름");
            request.setPrice(BigDecimal.valueOf(2000));
            request.setMenuGroupId(menuGroup.getId());
            request.setMenuProducts(List.of(menuProductRequest));
            request.setDisplayed(true);

            when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
            when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
            when(productRepository.findById(any())).thenReturn(Optional.of(product));

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
            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(BigDecimal.valueOf(2000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            Menu menu = new Menu();
            menu.setMenuProducts(List.of(menuProduct));
            menu.setPrice(BigDecimal.valueOf(1500));
            menu.setDisplayed(false);

            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // when
            Menu result = menuService.display(UUID.randomUUID());

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
            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(BigDecimal.valueOf(2000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            Menu menu = new Menu();
            menu.setMenuProducts(List.of(menuProduct));
            menu.setPrice(BigDecimal.valueOf(3000));
            menu.setDisplayed(false);

            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // when & then
            assertThrows(IllegalStateException.class, () -> menuService.display(UUID.randomUUID()));
        }
    }

    @Nested
    class ChangePriceTests {

        @DisplayName("메뉴의 가격을 변경할 수 있다.")
        @Test
        void changePriceWithValidInput() {
            // given
            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(BigDecimal.valueOf(2000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            Menu menu = new Menu();
            menu.setMenuProducts(List.of(menuProduct));
            menu.setPrice(BigDecimal.valueOf(1500));
            menu.setDisplayed(true);

            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(2000));

            // when
            Menu result = menuService.changePrice(product.getId(), request);

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
            Product product = new Product();
            product.setId(UUID.randomUUID());
            product.setPrice(BigDecimal.valueOf(2000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(1);

            Menu menu = new Menu();
            menu.setMenuProducts(List.of(menuProduct));
            menu.setPrice(BigDecimal.valueOf(1500));
            menu.setDisplayed(true);

            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            Menu request = new Menu();
            request.setPrice(BigDecimal.valueOf(3000));

            // when & then
            assertThrows(IllegalArgumentException.class,
                () -> menuService.changePrice(product.getId(), request));
        }
    }

}
