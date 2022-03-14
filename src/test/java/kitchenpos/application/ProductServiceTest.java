package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static kitchenpos.KitchenposFixture.*;
import static kitchenpos.fixture.MenuFixture.메뉴_리스트_가격_이만원;
import static kitchenpos.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private MenuRepository menuRepository;

  @Mock(lenient = true)
  private PurgomalumClient purgomalumClient;

  @InjectMocks
  private ProductService productService;

  @Test
  @DisplayName("상품을 생성합니다.")
  void createProduct() {
    //given
    Product request = 상품_가격_만원();

    //when
    when(productRepository.save(any())).thenReturn(request);

    //then
    assertDoesNotThrow(() -> {
      Product result = productService.create(request);
      assertThat(result.getPrice()).isEqualTo(request.getPrice());
    });
  }

  @Test
  @DisplayName("상품을 생성할 때, 상품 가격이 음수이면 IllegalArgumentException 예외 발생")
  void checkProducePrice1() {
    //given
    Product request = 상품_가격_음수();

    //then
    assertThatThrownBy(() -> {
      productService.create(request);
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("상품을 가격을 변경합니다.")
  void changeProductPrice() {
    //given
    Product request = 상품_가격_만원();
    List<Menu> menus = 메뉴_리스트_가격_이만원();

    when(productRepository.findById(any())).thenReturn(Optional.of(request));
    when(menuRepository.findAllByProductId(any())).thenReturn(menus);

    //then
    assertDoesNotThrow(() -> {
      Product result = productService.changePrice(ID, request);
    });
  }

  @Test
  @DisplayName("상품 가격을 변경할 때, 상품의 가격이 음수이면, IllegalArgumentException 예외가 발생")
  void checkProducePrice2() {
    //given
    Product request = 상품_가격_음수();

    //then
    assertThatThrownBy(() -> {
      productService.changePrice(ID, request);
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("메뉴의 가격이 메뉴에 올라간 상품의 가격의 합보다 크면 메뉴가 숨김 처리가 됩니다.")
  void checkMenuChange() {
    //given
    Product request = 상품_가격_만원();
    List<Menu> menus = 메뉴_리스트_가격_이만원();

    //when
    when(productRepository.findById(any())).thenReturn(Optional.of(request));
    when(menuRepository.findAllByProductId(any())).thenReturn(menus);

    //then
    assertDoesNotThrow(()->{
      productService.changePrice(ID, request);
    });
    menus.forEach(menu -> {
      assertThat(menu.isDisplayed()).isFalse();
    });
  }

  @Test
  @DisplayName("상품의 모든 정보를 가져옵니다.")
  void findAll() {
    //given
    when(productRepository.findAll()).thenReturn(상품_리스트_가격_만원());

    //then
    productService.findAll();
    verify(productRepository).findAll();
  }
}
