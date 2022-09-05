package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

  private MenuService menuService;

  @Mock
  private MenuRepository menuRepository;

  @Mock
  private MenuGroupRepository menuGroupRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private PurgomalumClient purgomalumClient;

  @BeforeEach
  void setUp() {
    this.menuService = new MenuService(
        menuRepository,
        menuGroupRepository,
        productRepository,
        purgomalumClient
    );
  }

  @DisplayName("유효한 메뉴 이름, 가격, 메뉴그룹, 진열상태, 메뉴상품들을 입력하면 등록된 메뉴를 반환한다.")
  @Test
  void givenValidMenu_whenCreate_thenReturnMenu() {
    // given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");

    Product product1 = createProduct("후라이드치킨", BigDecimal.valueOf(11000));
    Product product2 = createProduct("양념치킨", BigDecimal.valueOf(12000));

    List<Product> products = List.of(product1, product2);
    List<UUID> productIds = products.stream()
        .map(Product::getId)
        .collect(Collectors.toList());

    List<MenuProduct> menuProducts = List.of(
        createMenuProduct(product1, 1),
        createMenuProduct(product2, 1)
    );

    Menu menu = createMenu(
        "후라이드 + 양념치킨",
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    given(menuGroupRepository.findById(menuGroup.getId())).willReturn(Optional.of(menuGroup));
    given(productRepository.findAllByIdIn(productIds)).willReturn(products);
    given(productRepository.findById(product1.getId())).willReturn(Optional.of(product1));
    given(productRepository.findById(product2.getId())).willReturn(Optional.of(product2));
    given(purgomalumClient.containsProfanity(anyString())).willReturn(false);
    given(menuRepository.save(any(Menu.class))).willReturn(menu);

    // when
    Menu createdMenu = menuService.create(menu);

    // then
    assertThat(createdMenu.getId()).isNotNull();
    assertThat(createdMenu.getName()).isEqualTo(menu.getName());
    assertThat(createdMenu.getPrice()).isEqualTo(menu.getPrice());
    assertThat(createdMenu.isDisplayed()).isEqualTo(menu.isDisplayed());
    assertThat(createdMenu.getMenuGroup().getId()).isEqualTo(menuGroup.getId());
    assertThat(createdMenu.getMenuGroup().getName()).isEqualTo(menuGroup.getName());
    assertThat(createdMenu.getMenuProducts()).hasSize(2);
    assertThat(createdMenu.getMenuProducts()).containsAll(menuProducts);
  }

  @DisplayName("메뉴 가격은 0원 보다 작을 수 없다.")
  @MethodSource("provideBigDecimalsForNullAndNegative")
  @ParameterizedTest(name = "{displayName}: [{index}] {argumentsWithNames}")
  void givenNotValidPrice_whenCreate_thenIllegalArgumentException(BigDecimal price) {
    // given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");
    Product product1 = createProduct("후라이드치킨", BigDecimal.valueOf(11000));
    Product product2 = createProduct("양념치킨", BigDecimal.valueOf(12000));
    List<MenuProduct> menuProducts = List.of(
        createMenuProduct(product1, 1),
        createMenuProduct(product2, 1)
    );

    Menu menu = createMenu(
        "후라이드 + 양념치킨",
        price,
        true,
        menuGroup,
        menuProducts
    );

    // when & then
    assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
  }

  private static Menu createMenu(
      String name,
      BigDecimal price,
      boolean isDisplayed,
      MenuGroup menuGroup,
      List<MenuProduct> menuProducts
  ) {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setName(name);
    menu.setPrice(price);
    menu.setDisplayed(isDisplayed);
    menu.setMenuGroupId(menuGroup.getId());
    menu.setMenuGroup(menuGroup);
    menu.setMenuProducts(menuProducts);
    return menu;
  }

  private static MenuGroup createMenuGroup(String name) {
    MenuGroup menuGroup = new MenuGroup();
    menuGroup.setId(UUID.randomUUID());
    menuGroup.setName(name);
    return menuGroup;
  }

  private static MenuProduct createMenuProduct(Product product, int quantity) {
    MenuProduct menuProduct = new MenuProduct();
    menuProduct.setProductId(product.getId());
    menuProduct.setProduct(product);
    menuProduct.setQuantity(quantity);
    return menuProduct;
  }

  private static Product createProduct(String name, BigDecimal price) {
    Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setName(name);
    product.setPrice(price);
    return product;
  }

  private static Stream<Arguments> provideBigDecimalsForNullAndNegative() {
    return Stream.of(
        null,
        Arguments.of(BigDecimal.valueOf(-1))
    );
  }
}
