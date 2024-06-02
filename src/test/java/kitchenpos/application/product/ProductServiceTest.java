package kitchenpos.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import kitchenpos.application.ProductService;
import kitchenpos.common.UuidBuilder;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.common.FakeUuidBuilder;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.BadWordsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
  public static final long TWENTY_THOUSANDS = 20_000L;
  public static final String HANGANG_RAMEN = "한강라면";
  public static final long TEN_THOUSANDS = 10_000L;
  public static final String HANGANG_UDON = "HANGANG_UDON";
  public static final long THIRTY_THOUSANDS = 30_000L;
  public static final String RAMEN_GROUP = "RAMEN";
  private ProductService productService;
  private UuidBuilder fakeUuidBuilder;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private MenuRepository menuRepository;

  @Mock
  private BadWordsValidator badWordsValidator;

  @BeforeEach
  void setUp() {
    productService = new ProductService(productRepository, menuRepository, badWordsValidator);
    fakeUuidBuilder = new FakeUuidBuilder();
  }

  @Nested
  @DisplayName("상품(Product)을 생성한다.")
  class ProductRegistration {
    @Test
    @DisplayName("상품(Product)을 생성할 수 있다.")
    void createProduct() {
      final Product product = ProductFixture.createProduct(HANGANG_RAMEN, TWENTY_THOUSANDS);

      given(badWordsValidator.containsProfanity(any())).willReturn(false);
      given(productRepository.save(any())).willReturn(product);

      final Product actual = productService.create(product);

      assertAll(
              () -> assertThat(actual.getId()).isEqualTo(fakeUuidBuilder.createRandomUUID("SIMON")),
              () ->       assertThat(actual.getName()).isEqualTo(HANGANG_RAMEN),
              () ->       assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(TWENTY_THOUSANDS))
      );

    }


    @DisplayName("상품의 이름(Product Name)이 없거나 비속어(Profanity)가 포함될 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "badwords"})
    void failToCreateProductWithProfanityAndEmptyProductName(final String name) {
      final Product product = ProductFixture.createProduct(name, TWENTY_THOUSANDS);

      given(badWordsValidator.containsProfanity(any())).willReturn(true);

      assertThatIllegalArgumentException()
              .isThrownBy(() -> productService.create(product));
    }
  }

  @Nested
  @DisplayName("상품(Product)를 조회한다.")
  class ProductView {
    @Test
    @DisplayName("상품(Product)을 조회할 수 있다.")
    void viewProduct() {
      Product ramenProduct = ProductFixture.createProduct(HANGANG_RAMEN, TWENTY_THOUSANDS);
      Product udonProduct = ProductFixture.createProduct(HANGANG_UDON, TEN_THOUSANDS);

      given(productRepository.findAll()).willReturn(List.of(ramenProduct, udonProduct));

      final List<Product> actual = productService.findAll();

      assertThat(actual).containsExactly(ramenProduct, udonProduct);
    }

  }

  @Nested
  @DisplayName("상품 가격(Product Price)을 변경한다.")
  class ProductPriceChange {
    @DisplayName("상품 가격(Product Price)이 양수이어야 한다.")
    @ParameterizedTest
    @ValueSource(longs = {-5L, -1000L, -2000L})
    void failToPriceChangeWithNegativeProductPrice(Long price) {
      Product product = ProductFixture.createProduct(HANGANG_RAMEN, price);

      assertThatIllegalArgumentException()
              .isThrownBy(() -> productService.changePrice(product.getId(), product));
    }

    @Test
    @DisplayName("`메뉴 가격` (Menu Price)이 `메뉴 상품`(Menu Product)들의 가격의 총액 보다 높으면" +
            " `메뉴`(Menu)가 `전시 상태`(Menu Display Status)가 `숨김 상태(HIDE)` 로 바뀐다.")
    void MenuIncludingProductBecomesHiddenWithPriceIncreaseOnMenu() {
      Product product = ProductFixture.createProduct(HANGANG_RAMEN, THIRTY_THOUSANDS);
      MenuGroup menuGroup = MenuGroupFixture.createMenuGroup(RAMEN_GROUP);
      Menu menu = MenuFixture.createMenu(HANGANG_RAMEN, THIRTY_THOUSANDS, menuGroup, product, 1);

      given(productRepository.findById(any())).willReturn(Optional.of(product));
      given(menuRepository.findAllByProductId(any())).willReturn(List.of(menu));

      Product changeRequest = ProductFixture.createProduct(HANGANG_RAMEN, TWENTY_THOUSANDS);
      Product actual = productService.changePrice(product.getId(), changeRequest);

      assertAll(() -> assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(TWENTY_THOUSANDS)),
              () -> assertThat(menu.isDisplayed()).isEqualTo(false)
      );
    }
  }
}
