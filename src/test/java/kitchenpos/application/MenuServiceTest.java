package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
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

import static kitchenpos.KitchenposFixture.*;
import static kitchenpos.fixture.MenuFixture.*;
import static kitchenpos.fixture.MenuGroupFixture.정상_메뉴_그룹;
import static kitchenpos.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

  private static final Long NEGATIVE_NUM = -1L;

  @Mock
  MenuRepository menuRepository;
  @Mock
  MenuGroupRepository menuGroupRepository;
  @Mock
  ProductRepository productRepository;
  @Mock(lenient = true)
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
  @DisplayName("가게 점주는 메뉴를 추가 할 수 있습니다.")
  void addMenu() {
    //given
    Menu request = 정상_메뉴_가격_만원();
    MenuGroup menuGroup = 정상_메뉴_그룹();
    Product product = 상품_가격_만원();
    List<Product> productList = Collections.singletonList(product);

    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
    when(productRepository.findAllByIdIn(any())).thenReturn(productList);
    when(productRepository.findById(any())).thenReturn(Optional.of(product));

    //then
    assertDoesNotThrow(() -> {
      Menu menu = menuService.create(request);
      verify(menuRepository).save(any(Menu.class));
    });
  }

  @ParameterizedTest
  @MethodSource("menuPriceNullAndMinus")
  @DisplayName("메뉴의 가격이 존재하지 않거나 음수이면 IllegalArgumentException 예외 발생")
  void price(BigDecimal price) {
    //given
    Menu request = 메뉴_가격_입력(price);

    //then
    assertThatThrownBy(() -> menuService.create(request))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("생성하려는 메뉴에 메뉴 그룹이 존재하지 않으면 NoSuchElementException 예외 발생")
  void menuInMenuGroup() {
    //given
    Menu request = 메뉴_그룹_등록_안한_메뉴();

    //when
    when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

    //then
    assertThatThrownBy(() -> menuService.create(request))
      .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  @DisplayName("메뉴에 올라간 상품과 메뉴에 올라간 상품의 사이즈가 다르면 IllegalArgumentException 예외 발생")
  void checkProductAndMenuProduct() {
    //given
    Menu request = 메뉴의_메뉴_상품과_메뉴에_올라간_상품_사이즈_다름();
    MenuGroup menuGroup = request.getMenuGroup();

    //when
    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
    when(productRepository.findAllByIdIn(any())).thenReturn(상품_리스트_가격_만원_갯수_두개());

    //then
    assertThatThrownBy(() -> menuService.create(request)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @Order(5)
  @DisplayName("메뉴에 올라간 상품의 수량이 음수면 IllegalArgumentException 예외 발생")
  void checkQuantity() {
    //given
    Menu request = 메뉴_등록_메뉴_품목_수량_음수();
    MenuGroup menuGroup = request.getMenuGroup();
    List<Product> productList = 상품_리스트_가격_만원();

    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
    when(productRepository.findAllByIdIn(any())).thenReturn(productList);

    //then
    assertThatThrownBy(() -> menuService.create(request))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @Order(6)
  @DisplayName("메뉴의 상품들 가격의 합보다 메뉴 가격이 비싸면, IllegalArgumentException 예외 발생")
  void comparePrice() {
    //given
    Menu request = 정상_메뉴_가격_이만원();
    List<Product> productList = 상품_리스트_가격_만원();

    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(request.getMenuGroup()));
    when(productRepository.findAllByIdIn(any())).thenReturn(productList);
    when(productRepository.findById(any())).thenReturn(productList.stream().findFirst());

    //then
    assertThatThrownBy(() -> menuService.create(request))
      .isInstanceOf(IllegalArgumentException.class);
  }


  @Test
  @DisplayName("메뉴 이름이 존재해야하고, 비속어가 포함이 되면 안됩니다.")
  void needName() {
    //given
    Menu request = 정상_메뉴_가격_만원();
    List<Product> productList = 상품_리스트_가격_만원();

    when(menuGroupRepository.findById(any())).thenReturn(Optional.of(request.getMenuGroup()));
    when(productRepository.findAllByIdIn(any())).thenReturn(productList);
    when(productRepository.findById(any())).thenReturn(productList.stream().findFirst());
    //when
    when(purgomalumClient.containsProfanity(any())).thenReturn(true);

    //then
    assertThatThrownBy(() -> menuService.create(request))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("가게 점주는 가격을 변경할 수 있습니다.")
  void changePriceInMenu() {
    //given
    Menu request = 정상_메뉴_가격_만원();
    Menu menu = 정상_메뉴_가격_이만원();
    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    //then
    assertDoesNotThrow(() -> {
      Menu result = menuService.changePrice(ID, request);
      assertThat(result.getPrice()).isEqualTo(request.getPrice());
    });
  }

  @Test
  @DisplayName("변경하려는 메뉴의 가격은 음수면 IllegalArgumentException 예외 발생")
  void changePriceIsPositiveNum() {
    //given
    Menu request = 메뉴_등록_가격_음수();

    //then
    assertThatThrownBy(() -> menuService.changePrice(ID, request))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("메뉴 가격은 메뉴의 상품들 가격의 합보다 비싸면 IllegalArgumentException 예외 발생")
  void compareChangePrice() {
    //given
    Menu request = 정상_메뉴_가격_이만원();
    Menu menu = 정상_메뉴_가격_만원();

    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    //then
    assertThatThrownBy(() -> menuService.changePrice(ID, request))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("가게 점주는 메뉴 숨김 처리를 해제할 수 있습니다.")
  void uncoverMenu() {
    //given
    Menu menu = 정상_메뉴_가격_만원();

    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    //then
    assertDoesNotThrow(() -> {
      Menu result = menuService.display(ID);
      assertThat(result.isDisplayed()).isTrue();
    });
  }

  @Test
  @DisplayName("메뉴 가격은 메뉴의 상품들 가격의 합보다 비싸면 IllegalStateException 예외 발생")
  void checkMenuPrice() {
    //given
    Menu menu = 정상_메뉴_가격_오류();

    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    //then
    assertThatThrownBy(() -> menuService.display(ID))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  @DisplayName("점주는 메뉴를 숨김 처리할 수 있습니다.")
  void hideMenu() {
    //given
    Menu menu = 정상_메뉴_가격_만원();

    when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

    //then
    assertDoesNotThrow(() -> {
      Menu result = menuService.hide(ID);
      assertThat(result.isDisplayed()).isFalse();
    });
  }

  @Test
  @DisplayName("가게 점주와 가게 손님은 메뉴를 전부 조회할 수 있습니다.")
  void findAll() {
    //given
    when(menuRepository.findAll()).thenReturn(메뉴_리스트_가격_만원());

    //then
    assertDoesNotThrow(() -> {
      menuService.findAll();
      verify(menuRepository).findAll();
    });
  }
}
