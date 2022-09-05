package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
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

  @DisplayName("메뉴그룹이 존재하지 않으면 메뉴를 등록할 수 없다.")
  @Test
  void givenNoMenuGroup_whenCreate_thenNoSuchElementException() {
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
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(NoSuchElementException.class);
  }

  @DisplayName("요청 메뉴상품이 없거나 비어있을 수 없다.")
  @NullAndEmptySource
  @ParameterizedTest(name = "{displayName}: [{index}] {argumentsWithNames}")
  void givenEmptyMenuProduct_whenCreate_thenNoSuchElementException(List<MenuProduct> menuProducts) {
    // given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");

    Menu menu = createMenu(
        "후라이드 + 양념치킨",
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    given(menuGroupRepository.findById(menuGroup.getId())).willReturn(Optional.of(menuGroup));

    // when & then
    assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
  }

  @DisplayName("메뉴상품이 존재하지 않으면 메뉴를 등록할 수 없다.")
  @Test
  void givenNotFoundMenuProduct_whenCreate_thenIllegalArgumentException() {
    // given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");
    Product product1 = createProduct("후라이드치킨", BigDecimal.valueOf(11000));
    Product product2 = createProduct("양념치킨", BigDecimal.valueOf(12000));
    List<MenuProduct> menuProducts = List.of(
        createMenuProduct(product1, 1),
        createMenuProduct(product2, 1)
    );

    List<Product> products = List.of(product1, product2);
    List<UUID> productIds = products.stream()
        .map(Product::getId)
        .collect(Collectors.toList());

    Menu menu = createMenu(
        "후라이드 + 양념치킨",
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    given(menuGroupRepository.findById(menuGroup.getId())).willReturn(Optional.of(menuGroup));
    given(productRepository.findAllByIdIn(productIds)).willReturn(List.of(product1));

    // when & then
    assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
  }

  @DisplayName("메뉴상품 수량이 0개 보다 작을 수 없다.")
  @Test
  void givenNegativeQuantity_whenCreate_thenIllegalArgumentException() {
    // given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");
    Product product1 = createProduct("후라이드치킨", BigDecimal.valueOf(11000));
    Product product2 = createProduct("양념치킨", BigDecimal.valueOf(12000));
    List<MenuProduct> menuProducts = List.of(
        createMenuProduct(product1, -1),
        createMenuProduct(product2, 1)
    );

    List<Product> products = List.of(product1, product2);
    List<UUID> productIds = products.stream()
        .map(Product::getId)
        .collect(Collectors.toList());

    Menu menu = createMenu(
        "후라이드 + 양념치킨",
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    given(menuGroupRepository.findById(menuGroup.getId())).willReturn(Optional.of(menuGroup));
    given(productRepository.findAllByIdIn(productIds)).willReturn(products);

    // when & then
    assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
  }

  @DisplayName("메뉴가격은 메뉴의 포함된 메뉴상품금액(메뉴상품가격 * 수량)의 총액 보다 더 높을 수 없다.")
  @Test
  void givenPriceGreaterSumProductPrice_whenCreate_thenIllegalArgumentException() {
    // given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");
    Product product1 = createProduct("후라이드치킨", BigDecimal.valueOf(11000));
    Product product2 = createProduct("양념치킨", BigDecimal.valueOf(12000));
    List<MenuProduct> menuProducts = List.of(
        createMenuProduct(product1, 1),
        createMenuProduct(product2, 1)
    );

    List<Product> products = List.of(product1, product2);
    List<UUID> productIds = products.stream()
        .map(Product::getId)
        .collect(Collectors.toList());

    Menu menu = createMenu(
        "후라이드 + 양념치킨",
        BigDecimal.valueOf(23100),
        true,
        menuGroup,
        menuProducts
    );

    given(menuGroupRepository.findById(menuGroup.getId())).willReturn(Optional.of(menuGroup));
    given(productRepository.findAllByIdIn(productIds)).willReturn(products);
    given(productRepository.findById(product1.getId())).willReturn(Optional.of(product1));
    given(productRepository.findById(product2.getId())).willReturn(Optional.of(product2));

    // when & then
    assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
  }

  @DisplayName("메뉴 이름은 비어있을 수 없다.")
  @NullSource
  @ParameterizedTest(name = "{displayName}: [{index}] {argumentsWithNames}")
  void givenEmptyName_whenCreate_thenNoSuchElementException(String name) {
    // given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");
    Product product1 = createProduct("후라이드치킨", BigDecimal.valueOf(11000));
    Product product2 = createProduct("양념치킨", BigDecimal.valueOf(120500));
    List<MenuProduct> menuProducts = List.of(
        createMenuProduct(product1, 1),
        createMenuProduct(product2, 1)
    );

    List<Product> products = List.of(product1, product2);
    List<UUID> productIds = products.stream()
        .map(Product::getId)
        .collect(Collectors.toList());

    Menu menu = createMenu(
        name,
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    given(menuGroupRepository.findById(menuGroup.getId())).willReturn(Optional.of(menuGroup));
    given(productRepository.findAllByIdIn(productIds)).willReturn(products);
    given(productRepository.findById(product1.getId())).willReturn(Optional.of(product1));
    given(productRepository.findById(product2.getId())).willReturn(Optional.of(product2));

    // when & then
    assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
  }

  @DisplayName("메뉴 이름에는 비속어를 포함할 수 없다.")
  @Test
  void givenProfanityName_whenCreate_thenNoSuchElementException() {
    // given
    MenuGroup menuGroup = createMenuGroup("추천메뉴");
    Product product1 = createProduct("후라이드치킨", BigDecimal.valueOf(11000));
    Product product2 = createProduct("양념치킨", BigDecimal.valueOf(12000));
    List<MenuProduct> menuProducts = List.of(
        createMenuProduct(product1, 1),
        createMenuProduct(product2, 1)
    );

    List<Product> products = List.of(product1, product2);
    List<UUID> productIds = products.stream()
        .map(Product::getId)
        .collect(Collectors.toList());

    Menu menu = createMenu(
        "Shit",
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    given(menuGroupRepository.findById(menuGroup.getId())).willReturn(Optional.of(menuGroup));
    given(productRepository.findAllByIdIn(productIds)).willReturn(products);
    given(productRepository.findById(product1.getId())).willReturn(Optional.of(product1));
    given(productRepository.findById(product2.getId())).willReturn(Optional.of(product2));
    given(purgomalumClient.containsProfanity(anyString())).willReturn(true);

    // when & then
    assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
  }

  @DisplayName("메뉴 가격은 메뉴ID와 변경할 메뉴가격 정보를 입력하여 변경할 수 있다.")
  @Test
  void givenChangePrice_whenChangePrice_thenReturnChangedMenu() {
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
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

    Menu changePriceMenu = new Menu();
    changePriceMenu.setPrice(BigDecimal.valueOf(22000));

    // when
    Menu changedMenu = menuService.changePrice(menu.getId(), changePriceMenu);

    // then
    assertThat(changedMenu.getId()).isNotNull();
    assertThat(changedMenu.getName()).isEqualTo(menu.getName());
    assertThat(changedMenu.getPrice()).isEqualTo(changedMenu.getPrice());
    assertThat(changedMenu.isDisplayed()).isEqualTo(menu.isDisplayed());
    assertThat(changedMenu.getMenuGroup().getId()).isEqualTo(menuGroup.getId());
  }

  @DisplayName("메뉴 가격 변경시 메뉴 가격은 0원 보다 작을 수 없다.")
  @Test
  void givenNotValidPrice_whenChangePrice_thenIllegalArgumentException() {
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
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    Menu changePriceMenu = new Menu();
    changePriceMenu.setPrice(BigDecimal.valueOf(-1));

    // when & then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> menuService.changePrice(menu.getId(), changePriceMenu));
  }

  @DisplayName("메뉴가 존재하지 않으면 메뉴 가격을 변경할 수 없다.")
  @Test
  void givenNotFoundMenu_whenChangePrice_thenNoSuchElementException() {
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
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    given(menuRepository.findById(menu.getId())).willReturn(Optional.empty());

    Menu changePriceMenu = new Menu();
    changePriceMenu.setPrice(BigDecimal.valueOf(22000));

    // when & then
    assertThatThrownBy(() -> menuService.changePrice(menu.getId(), changePriceMenu))
        .isInstanceOf(NoSuchElementException.class);
  }

  @DisplayName("메뉴가격변경 - 메뉴가격은 메뉴의 포함된 메뉴상품금액(메뉴상품가격 * 수량)의 총액 보다 더 높을 수 없다.")
  @Test
  void givenHighPrice_whenChangePrice_thenIllegalArgumentException() {
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
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

    Menu changePriceMenu = new Menu();
    changePriceMenu.setPrice(BigDecimal.valueOf(23100));

    // when & then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> menuService.changePrice(menu.getId(), changePriceMenu));
  }

  @DisplayName("메뉴는 메뉴ID를 통해 메뉴를 진열할 수 있다.")
  @Test
  void givenMenuId_whenDisplay_thenReturnDisplayedMenu() {
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
        BigDecimal.valueOf(23000),
        false,
        menuGroup,
        menuProducts
    );

    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

    // when
    Menu changedMenu = menuService.display(menu.getId());

    // then
    assertThat(changedMenu.getId()).isNotNull();
    assertThat(changedMenu.getName()).isEqualTo(menu.getName());
    assertThat(changedMenu.getPrice()).isEqualTo(menu.getPrice());
    assertThat(changedMenu.isDisplayed()).isTrue();
    assertThat(changedMenu.getMenuGroup().getId()).isEqualTo(menuGroup.getId());
    assertThat(changedMenu.getMenuGroup().getName()).isEqualTo(menuGroup.getName());
    assertThat(changedMenu.getMenuProducts()).hasSize(2);
    assertThat(changedMenu.getMenuProducts()).containsAll(menuProducts);
  }

  @DisplayName("메뉴가 존재하지 않으면 메뉴를 진열할 수 없다.")
  @Test
  void givenNotFoundMenu_whenDisplay_thenNoSuchElementException() {
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
        BigDecimal.valueOf(23000),
        false,
        menuGroup,
        menuProducts
    );

    given(menuRepository.findById(menu.getId())).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> menuService.display(menu.getId()))
        .isInstanceOf(NoSuchElementException.class);
  }

  @DisplayName("메뉴진열 - 메뉴가격은 메뉴의 포함된 메뉴상품금액(메뉴상품가격 * 수량)의 총액 보다 더 높을 수 없다.")
  @Test
  void givenHighPrice_whenDisplay_thenIllegalArgumentException() {
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
        BigDecimal.valueOf(23100),
        true,
        menuGroup,
        menuProducts
    );

    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

    // when & then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> menuService.display(menu.getId()));
  }

  @DisplayName("메뉴는 메뉴ID를 통해 메뉴를 숨길 수 있다.")
  @Test
  void givenMenuId_whenHide_thenReturnHideMenu() {
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
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

    // when
    Menu changedMenu = menuService.hide(menu.getId());

    // then
    assertThat(changedMenu.getId()).isNotNull();
    assertThat(changedMenu.getName()).isEqualTo(menu.getName());
    assertThat(changedMenu.getPrice()).isEqualTo(menu.getPrice());
    assertThat(changedMenu.isDisplayed()).isFalse();
    assertThat(changedMenu.getMenuGroup().getId()).isEqualTo(menuGroup.getId());
    assertThat(changedMenu.getMenuGroup().getName()).isEqualTo(menuGroup.getName());
    assertThat(changedMenu.getMenuProducts()).hasSize(2);
    assertThat(changedMenu.getMenuProducts()).containsAll(menuProducts);
  }

  @DisplayName("메뉴가 존재하지 않으면 숨길 수 없다.")
  @Test
  void givenNotFoundMenu_whenHide_thenNoSuchElementException() {
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
        BigDecimal.valueOf(23000),
        true,
        menuGroup,
        menuProducts
    );

    given(menuRepository.findById(menu.getId())).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> menuService.display(menu.getId()))
        .isInstanceOf(NoSuchElementException.class);
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
