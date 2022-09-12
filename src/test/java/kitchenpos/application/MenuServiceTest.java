package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.application.fixture.MenuProductFixture;
import kitchenpos.application.fixture.ProductFixture;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("메뉴 그룹")
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

  @InjectMocks
  private MenuService menuService;

  private Product product;
  private MenuGroup menuGroup;
  private MenuProduct menuProduct;
  private Menu menu;

  @BeforeEach
  void setUp() {
    menu = MenuFixture.createMenu();
    product = ProductFixture.createProduct();
    menuGroup = MenuGroupFixture.createMenuGroup();
    menuProduct = MenuProductFixture.createMenuProduct();
  }

  @DisplayName("메뉴 등록")
  @Test
  void createMenu() {
    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
    when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
    when(productRepository.findById(any())).thenReturn(Optional.of(product));
    when(menuRepository.save(any())).thenReturn(menu);

    Menu result = menuService.create(menu);

    assertThat(result.getName()).isEqualTo("후라이드+후라이드");
  }

  @DisplayName("메뉴 가격 null 등록 에러")
  @Test
  void createMenuPriceNull() {
    menu.setPrice(null);

    assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("메뉴 가격 음수 등록 에러")
  @Test
  void createMenuPriceNegative() {
    menu.setPrice(BigDecimal.valueOf(-1));

    assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("메뉴 메뉴상품 null 등록 에러")
  @Test
  void createMenuMenuProductNull() {
    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

    menu.setMenuProducts(null);

    assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("메뉴 메뉴상품 빈값 등록 에러")
  @Test
  void createMenuMenuProductEmpty() {
    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

    menu.setMenuProducts(List.of());

    assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("메뉴상품 중 수량이 음수이면 에러")
  @Test
  void menuProductQuantityNegative() {
    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
    when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));

    menuProduct.setQuantity(-1);
    menu.setMenuProducts(List.of(menuProduct));

    assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("메뉴 가격이 메뉴상품의 총합보다 크면 에러")
  @Test
  void menuPriceMenuProductTotalPriceCompare() {
    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
    when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
    when(productRepository.findById(any())).thenReturn(Optional.of(product));

    menu.setPrice(BigDecimal.valueOf(50000));

    assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("메뉴 이름이 null이면 에러")
  @Test
  void menuNameNull() {
    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
    when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
    when(productRepository.findById(any())).thenReturn(Optional.of(product));

    menu.setName(null);

    assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("메뉴 가격 변경")
  @Test
  void chageMenuPrice() {
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    Menu chageMenu = new Menu();
    chageMenu.setPrice(BigDecimal.valueOf(20000));

    Menu result = menuService.changePrice(menu.getId(), chageMenu);

    assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(20000));
  }

  @DisplayName("메뉴 가격 음수 수정일 경우 에러")
  @Test
  void chageMenuPriceNegative() {
    Menu chageMenu = new Menu();
    chageMenu.setPrice(BigDecimal.valueOf(-1));

    assertThatThrownBy(() -> menuService.changePrice(menu.getId(), chageMenu)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("메뉴 가격 메뉴상품의 총합보다 크게 수정일 경우 에러")
  @Test
  void chageMenuPriceMenuProduceTotalCompare() {
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    Menu chageMenu = new Menu();
    chageMenu.setPrice(BigDecimal.valueOf(50000));

    assertThatThrownBy(() -> menuService.changePrice(menu.getId(), chageMenu)).isInstanceOf(IllegalArgumentException.class);
  }

  @DisplayName("메뉴 노출로 변경")
  @Test
  void chageDisplay() {
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    menu.setDisplayed(false);

    Menu result = menuService.display(menu.getId());

    assertThat(result.isDisplayed()).isEqualTo(true);
  }

  @DisplayName("메뉴 숨김으로 변경")
  @Test
  void chageDisplayHide() {
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    menu.setDisplayed(true);

    Menu result = menuService.hide(menu.getId());

    assertThat(result.isDisplayed()).isEqualTo(false);
  }
}
