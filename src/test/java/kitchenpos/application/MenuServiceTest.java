package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
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

    @DisplayName("`메뉴`를 생성할 수 있다.")
    @Test
    void createMenuWithValidInput() {
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
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(menuRepository.save(any())).then(invocation -> invocation.getArgument(0));

        Menu result = menuService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(request.getName());
        assertThat(result.getPrice()).isEqualTo(request.getPrice());
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMenuGroup()).isEqualTo(menuGroup);
        assertThat(result.getMenuProducts()).hasSize(1);
        assertThat(result.isDisplayed()).isTrue();
    }

    @Test
    void createMenuWithNegativePrice() {
        Menu request = new Menu();
        request.setName("Valid Menu");
        request.setPrice(BigDecimal.valueOf(-1000));

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
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(1000));

        MenuProduct menuProductRequest = new MenuProduct();
        menuProductRequest.setProductId(product.getId());
        menuProductRequest.setQuantity(1);

        Menu request = new Menu();
        request.setName(null);
        request.setPrice(BigDecimal.valueOf(1000));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(List.of(menuProductRequest));
        request.setDisplayed(true);

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
    }

    @Test
    void createMenuWithProfanityInName() {
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

        assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
    }

    @DisplayName("지정한 '메뉴 그룹'이 존재하지 않으면 생성할 수 없다.")
    @Test
    void createMenuWithEmptyMenuProducts() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());

        Menu request = new Menu();
        request.setName("메뉴 이름");
        request.setPrice(BigDecimal.valueOf(1000));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(List.of());
        request.setDisplayed(true);

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

        assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
    }

    @DisplayName("지정한 '메뉴 그룹'이 존재하지 않으면 생성할 수 없다.")
    @Test
    void createMenuWithNullMenuProducts() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());

        Menu request = new Menu();
        request.setName("메뉴 이름");
        request.setPrice(BigDecimal.valueOf(1000));
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(null);
        request.setDisplayed(true);

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

        assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
    }

    @DisplayName("지정한 '메뉴 상품' 중 하나라도 '상품'이 존재하지 않으면 생성할 수 없다.")
    @Test
    void createMenuWithInvalidMenuProduct() {
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

        assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
    }

    @DisplayName("지정한 '메뉴 상품' 중 수량이 음수인 경우 생성할 수 없다.")
    @Test
    void createMenuWithNegativeQuantity() {
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

        assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
    }

    @DisplayName("메뉴의 가격의 합이 메뉴의 가격보다 작은 경우 생성할 수 없다.")
    @Test
    void createMenuWithInvalidPrice() {
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

        assertThrows(IllegalArgumentException.class, () -> menuService.create(request));
    }

    @DisplayName("메뉴의 가격을 변경할 수 있다.")
    @Test
    void changePriceWithValidInput() {
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

        Menu result = menuService.changePrice(product.getId(), request);

        assertThat(result).isNotNull();
        assertThat(result.getPrice()).isEqualTo(request.getPrice());
    }

    @DisplayName("메뉴의 가격이 null이면 변경할 수 없다.")
    @Test
    void changePriceWithNullPrice() {
        Menu request = new Menu();
        request.setPrice(null);

        assertThrows(IllegalArgumentException.class,
            () -> menuService.changePrice(UUID.randomUUID(), request));
    }

    @DisplayName("메뉴의 가격이 음수이면 변경할 수 없다.")
    @Test
    void changePriceWithNegativePrice() {
        Menu request = new Menu();
        request.setPrice(BigDecimal.valueOf(-1000));

        assertThrows(IllegalArgumentException.class,
            () -> menuService.changePrice(UUID.randomUUID(), request));
    }

    @DisplayName("`메뉴`의 가격 변경 시, 변경된 가격은 `메뉴 상품`들의 가격 총합을 초과할 수 없다.")
    @Test
    void changePriceWithInvalidPrice() {
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

        assertThrows(IllegalArgumentException.class,
            () -> menuService.changePrice(product.getId(), request));
    }

    @DisplayName("메뉴를 화면에 표시할 수 있다.")
    @Test
    void displayMenu() {
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

        Menu result = menuService.display(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result.isDisplayed()).isTrue();
    }

    @DisplayName("`메뉴` 노출 시, `메뉴`의 가격이 `메뉴 상품`들의 가격 총합을 초과하면 안된다.")
    @Test
    void displayMenuWithInvalidPrice() {
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

        assertThrows(IllegalStateException.class, () -> menuService.display(UUID.randomUUID()));
    }

    @DisplayName("메뉴를 화면에서 숨길 수 있다.")
    @Test
    void hideMenu() {
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

        Menu result = menuService.hide(UUID.randomUUID());

        assertThat(result).isNotNull();
        assertThat(result.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 조회할 수 있다.")
    @Test
    void findAllMenus() {
        when(menuRepository.findAll()).thenReturn(Collections.emptyList());

        List<Menu> menus = menuService.findAll();

        assertThat(menus).isNotNull();
        assertThat(menus).isEmpty();
    }
}
