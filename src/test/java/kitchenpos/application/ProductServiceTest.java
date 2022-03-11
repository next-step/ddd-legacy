package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static kitchenpos.KitchenposFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
  private static final BigDecimal MENU_PRICE = BigDecimal.valueOf(10000L);
  private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(10000L);
  private static final BigDecimal CHANGE_PRICE = BigDecimal.valueOf(20000L);
  private static final String PRODUCT_NAME = "product name";
  private static final long POSITIVE_NUM = 1L;
  private static final long NEGATIVE_NUM = -1L;
  private static final long ZERO = 0L;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private MenuRepository menuRepository;

  @Mock
  private PurgomalumClient purgomalumClient;

  @InjectMocks
  private ProductService productService;

  private static Stream<BigDecimal> menuPriceNullAndMinus() {
    return Stream.of(
            BigDecimal.valueOf(NEGATIVE_NUM),
            null
    );
  }

  @Test
  @DisplayName("상품을 생성합니다.")
  void createProduct() {
    //given
    Product request = product();

    //then
    assertDoesNotThrow(() -> {
      productService.create(request);
    });
  }

  @ParameterizedTest
  @MethodSource("menuPriceNullAndMinus")
  @DisplayName("상품을 생성할 때, 상품 가격이 음수이면 IllegalArgumentException 예외 발생")
  void checkProducePrice1(BigDecimal price) {
    //given
    Product request = product();
    request.setPrice(price);

    //then
    assertThatThrownBy(() -> {
      productService.create(request);
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("상품을 가격을 변경합니다.")
  void changeProductPrice() {
    //given
    Product request = product();
    Menu menu = menu();

    request.setPrice(CHANGE_PRICE);
    menu.setPrice(MENU_PRICE);

    menu.getMenuProducts().forEach(menuProduct -> {
      menuProduct.getProduct().setPrice(PRODUCT_PRICE);
      menuProduct.setQuantity(POSITIVE_NUM);
    });
    when(productRepository.findById(any())).thenReturn(Optional.of(mock(Product.class)));
    when(menuRepository.findAllByProductId(any())).thenReturn(Collections.singletonList(menu));

    //then
    assertDoesNotThrow(() -> {
      Product product = productService.changePrice(UUID, request);
    });
  }


  @ParameterizedTest
  @MethodSource("menuPriceNullAndMinus")
  @DisplayName("상품 가격을 변경할 때, 상품의 가격이 음수이면, IllegalArgumentException 예외가 발생")
  void checkProducePrice2(BigDecimal price) {
    //given
    Product request = product();
    request.setPrice(price);

    //then
    assertThatThrownBy(() -> {
      productService.changePrice(UUID, request);
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("메뉴의 가격이 메뉴에 올라간 상품의 가격의 합보다 크면 메뉴가 숨김 처리가 됩니다.")
  void checkMenuChange() {
    //given
    Product request = product();
    Menu menu = menu();

    request.setPrice(PRODUCT_PRICE);
    menu.setPrice(MENU_PRICE);
    //when
    menu.setDisplayed(true);
    menu.getMenuProducts().forEach(menuProduct -> {
      menuProduct.getProduct().setPrice(CHANGE_PRICE);
      menuProduct.setQuantity(ZERO);
    });
    when(productRepository.findById(any())).thenReturn(Optional.of(mock(Product.class)));
    when(menuRepository.findAllByProductId(any())).thenReturn(Collections.singletonList(menu));

    //then
    productService.changePrice(UUID, request);
    assertThat(menu.isDisplayed()).isFalse();

  }

  @Test
  @DisplayName("상품의 모든 정보를 가져옵니다.")
  void findAll() {
    //given
    when(productRepository.findAll()).thenReturn(Collections.singletonList(product()));

    //then
    productService.findAll();
    verify(productRepository).findAll();
  }
}
