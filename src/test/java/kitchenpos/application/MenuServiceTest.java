package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.*;
import static kitchenpos.KitchenposFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

  private static final long POSITIVE_NUM = 1L;
  private static final Long NEGATIVE_NUM = -1L;

  @Mock
  MenuRepository menuRepository;
  @Mock
  MenuGroupRepository menuGroupRepository;
  @Mock
  ProductRepository productRepository;
  @Mock
  PurgomalumClient purgomalumClient;

  @InjectMocks
  private MenuService menuService;

  private static Stream<BigDecimal> menuPriceNullAndMinus() {
    return Stream.of(
            BigDecimal.valueOf(NEGATIVE_NUM),
            null
    );
  }

  @Test
  @Order(1)
  @DisplayName("가게 점주는 메뉴를 추가 할 수 있습니다.")
  void addMenu() {
    //given
    Menu request = menu();
    MenuGroup menuGroup = menuGroup();
    Product product = product();
    List<Product> productList = Collections.singletonList(product);

    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
    when(productRepository.findAllByIdIn(any())).thenReturn(productList);
    when(productRepository.findById(any())).thenReturn(Optional.of(product));

    //then
    Assertions.assertDoesNotThrow(() -> {
      Menu menu = menuService.create(request);
    });
  }

  @Order(2)
  @ParameterizedTest
  @MethodSource("menuPriceNullAndMinus")
  @DisplayName("메뉴의 가격이 존재하지 않거나 음수이면 IllegalArgumentException 예외 발생")
  void price(BigDecimal price) {
    //given
    Menu request = menu();

    //when
    request.setPrice(price);

    //then
    assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Order(3)
  @Test
  @DisplayName("생성하려는 메뉴에 메뉴 그룹이 존재하지 않으면 NoSuchElementException 예외 발생")
  void menuInMenuGroup() {
    //given
    Menu request = menu();

    //when
    when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

    //then
    assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(NoSuchElementException.class);
  }

  @Order(4)
  @Test
  @DisplayName("메뉴에 올라간 상품과 메뉴에 올라간 상품의 사이즈가 다르면 IllegalArgumentException 예외 발생")
  void checkProductAndMenuProduct() {
    //given
    Menu request = menu();
    MenuGroup menuGroup = menuGroup();
    Product product = product();

    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
    //when
    List<Product> productList = Arrays.asList(product, product);
    when(productRepository.findAllByIdIn(any())).thenReturn(productList);

    //then
    assertThatThrownBy(() -> {
      menuService.create(request);
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @Order(5)
  @DisplayName("메뉴에 올라간 상품의 수량이 음수면 IllegalArgumentException 예외 발생")
  void checkQuantity() {
    //given
    Menu request = menu();
    MenuGroup menuGroup = menuGroup();
    MenuProduct menuProduct = menuProduct();
    Product product = product();
    List<Product> productList = Arrays.asList(product, product);

    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
    when(productRepository.findAllByIdIn(any())).thenReturn(productList);

    //when
    menuProduct.setQuantity(NEGATIVE_NUM);

    //then
    assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @Order(6)
  @DisplayName("메뉴 가격은 메뉴의 상품들 가격의 합보다 비싸면, IllegalArgumentException 예외 발생")
  void comparePrice() {
    //given
    Menu request = menu();
    Product product = product();
    List<Product> productList = Collections.singletonList(product);

    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(request.getMenuGroup()));
    when(productRepository.findAllByIdIn(any())).thenReturn(productList);

    //when
    product.setPrice(BigDecimal.ZERO);
    when(productRepository.findById(any())).thenReturn(Optional.of(product));

    //then
    assertThatThrownBy(() -> menuService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @Order(7)
  @DisplayName("가게 점주는 가격을 변경할 수 있습니다.")
  void changePriceInMenu() {
    //given
    BigDecimal changePrice = BigDecimal.valueOf(1000);
    Menu request = menu();
    request.setPrice(changePrice);
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu()));

    //then
    Menu changePriceMenu = menuService.changePrice(UUID, request);
    assertThat(changePriceMenu.getPrice()).isEqualTo(changePrice);
  }

  @Order(8)
  @ParameterizedTest
  @MethodSource("menuPriceNullAndMinus")
  @DisplayName("변경하려는 메뉴의 가격은 0원 이상입니다.")
  void changePriceIsPositiveNum(BigDecimal price) {
    //given
    Menu request = menu();

    //when
    request.setPrice(price);

    //then
    assertThatThrownBy(() -> menuService.changePrice(UUID, request))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @Order(9)
  @DisplayName("메뉴 가격은 메뉴의 상품들 가격의 합보다 비싸면 IllegalArgumentException 예외 발생")
  void compareChangePrice() {
    //given
    Menu request = menu();
    Menu menu = menu();

    when(menuRepository.findById(any())).thenReturn(Optional.ofNullable(menu));

    //when
    requireNonNull(menu).getMenuProducts().forEach(menuProduct -> {
      menuProduct.getProduct().setPrice(BigDecimal.ZERO);
      menuProduct.setQuantity(POSITIVE_NUM);
    });

    //then
    assertThatThrownBy(() -> menuService.changePrice(UUID, request))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @Order(10)
  @DisplayName("가게 점주는 메뉴가 숨김 처리를 해제할 수 있습니다.")
  void uncoverMenu() {
    //given
    Menu menu = menu();

    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    //then
    menuService.display(UUID);
    assertThat(menu.isDisplayed()).isTrue();
  }

  @Test
  @Order(11)
  @DisplayName("메뉴 가격은 메뉴의 상품들 가격의 합보다 비싸면 IllegalStateException 예외 발생")
  void checkMenuPrice() {
    //given
    Menu menu = menu();

    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    //when
    menu.getMenuProducts().forEach(menuProduct -> {
      menuProduct.getProduct().setPrice(BigDecimal.ZERO);
      menuProduct.setQuantity(POSITIVE_NUM);
    });

    //then
    assertThatThrownBy(() -> menuService.display(UUID))
            .isInstanceOf(IllegalStateException.class);
  }

  @Test
  @Order(12)
  @DisplayName("점주는 메뉴를 숨김 처리할 수 있습니다.")
  void hideMenu() {
    //given
    Menu menu = menu();

    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    //then
    menuService.hide(UUID);
    assertThat(menu.isDisplayed()).isFalse();
  }

  @Test
  @Order(13)
  @DisplayName("가게 점주와 가게 손님은 메뉴를 전부 조회할 수 있습니다.")
  void findAll() {
    //given
    Menu menu = menu();

    when(menuRepository.findAll()).thenReturn(Collections.singletonList(menu));

    //then
    menuService.findAll();
    verify(menuRepository).findAll();
  }
}
