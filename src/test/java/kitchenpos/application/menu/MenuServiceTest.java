package kitchenpos.application.menu;

import kitchenpos.application.MenuService;
import kitchenpos.domain.common.FakeUuidBuilder;
import kitchenpos.domain.*;
import kitchenpos.infra.menu.FakeMenuGroupRepository;
import kitchenpos.infra.menu.FakeMenuRepository;
import kitchenpos.infra.product.FakeProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.FakeBadWordsValidator;
import kitchenpos.infra.BadWordsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuServiceTest {
  public static final long TWENTY_THOUSANDS = 20_000L;
  public static final int THRITY_THOUSANDS = 30_000;
  public static final int FIFTY_THOUSANDS = 50_000;
  public static final String UDON = "우동";
  public static final String FOR_TWO = "이인용";
  public static final String TWO_UDONS = "우동과라면";

  private MenuGroupRepository menuGroupRepository;
  private MenuRepository menuRepository;
  private MenuService menuService;
  private ProductRepository productRepository;
  private BadWordsValidator badWordsValidator;
  private FakeUuidBuilder fakeUuidBuilder;
  private MenuGroup menuGroup;

  @BeforeEach
  void setUp() {
    menuRepository = new FakeMenuRepository();
    menuGroupRepository = new FakeMenuGroupRepository();
    badWordsValidator = new FakeBadWordsValidator();
    productRepository = new FakeProductRepository();
    fakeUuidBuilder = new FakeUuidBuilder();

    menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
        badWordsValidator);

    menuGroup = MenuGroupFixture.createMenuGroup(FOR_TWO);
    menuGroupRepository.save(menuGroup);
  }

  @Nested
  @DisplayName("메뉴(Menu)를 생성한다.")
  class MenuRegistration {
    @Test
    @DisplayName("메뉴(Menu)를 생성할 수 있다.")
    void createMenu() {
      Product productUdon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));
      Menu actual = menuService.create(MenuFixture.createMenu(TWO_UDONS, TWENTY_THOUSANDS, menuGroup, productUdon, 2));

      assertAll(
              () -> assertThat(actual.getName()).isEqualTo(TWO_UDONS),
              () -> assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(TWENTY_THOUSANDS))
      );
    }

    @DisplayName("`메뉴`는 이름은 정상적인 값이어야한다")
    @ParameterizedTest
    @NullSource
    void failToCreateMenuWithEmptyString(final String name) {
      Product productUdon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.create(MenuFixture.createMenu(name, THRITY_THOUSANDS, menuGroup, productUdon, 2)));
    }

    @DisplayName("`메뉴 가격`은 양수이어야한다.")
    @ParameterizedTest
    @ValueSource(ints = {-324234, -234})
    void failToCreateProductWithProfanityAndEmptyProductName(final int price) {
      Product productUdon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.create(MenuFixture.createMenu(TWO_UDONS, price, menuGroup, productUdon, 2)));
    }

    @DisplayName("`메뉴`는 `상품`들(1개 이상)을 가진다.")
    void faileToCreateWithZeroProduct() {
      Product productUdon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.create(MenuFixture.createMenu(TWO_UDONS, THRITY_THOUSANDS, menuGroup, productUdon, 0)));
    }

    @DisplayName("`메뉴`는 `메뉴상품`들의 총액을 넘을 수 없다.")
    void failToCreateWithHigherPriceThanMenuProductsPrices() {
      Product productUdon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.create(MenuFixture.createMenu(TWO_UDONS, FIFTY_THOUSANDS, menuGroup, productUdon, 2)));
    }
  }

  @Nested
  @DisplayName("메뉴(Menu)를 조회한다.")
  class MenuCateogryRegistration {
    @Test
    @DisplayName("메뉴(Menu)를 조회 할 수 있다.")
    void createProduct() {
      Product productUdon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));

      Menu udonMenu = menuService.create(MenuFixture.createMenu(TWO_UDONS, TWENTY_THOUSANDS, menuGroup, productUdon, 2));

      List<Menu> actual = menuService.findAll();

      assertThat(actual).contains(udonMenu);
    }

  }

  @Nested
  @DisplayName("`사장님`이 `메뉴 가격`을 변경할 수 있다.")
  class MenuPriceChange {
    @Test
    @DisplayName("`메뉴`는 `메뉴상품`들의 총액을 넘을 수 없다.")
    void changeMenuPriceWithHigherPriceThanMenuProductsPrices() {
      Product productUdon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));

      Menu menu = menuService.create(MenuFixture.createMenu(fakeUuidBuilder, TWO_UDONS, TWENTY_THOUSANDS, menuGroup, productUdon, 2));

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.changePrice(menu.getId(), MenuFixture.createMenu(TWO_UDONS, FIFTY_THOUSANDS, menuGroup, productUdon, 2)));
    }

    @DisplayName("`메뉴 가격`은 0보다 커야한다.")
    @ParameterizedTest
    @ValueSource(ints = {-324234, -234, -1})
    void changeMenuPrice(int price) {
      Product productUdon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));

      Menu menu = menuService.create(MenuFixture.createMenu(fakeUuidBuilder, TWO_UDONS, TWENTY_THOUSANDS, menuGroup, productUdon, 2));

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.changePrice(menu.getId(), MenuFixture.createMenu(TWO_UDONS, price, menuGroup, productUdon, 2)));
    }
  }

  @Nested
  @DisplayName("`사장님`이 `메뉴`를 `공개 상태` 상태를 변경할 수 있다.")
  class MenuDisplayStatus {
    @DisplayName("`메뉴`가 `메뉴 상품`들의 `가격`의 총액보다 `가격`이 클 수 없다.")
    @ParameterizedTest
    @ValueSource(longs = {10000000, 41000, 40001})
    void changeDisplayStatusWithHigherPriceThanMenuProducts(long price) {

      Product productUdon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));
      Menu udonMenu = menuService.create(MenuFixture.createMenu(fakeUuidBuilder, TWO_UDONS, THRITY_THOUSANDS, menuGroup, productUdon, 2));


      assertThatIllegalArgumentException()
              .isThrownBy(() ->
                      menuService.changePrice(udonMenu.getId(),
                              MenuFixture.createMenu(fakeUuidBuilder, TWO_UDONS, price, menuGroup, productUdon, 2)));
    }

    @DisplayName("`메뉴`가 존재하지 않으면 `공개 상태(VISIBLE)` 할 수 없다.")
    void showMenuWithNotExistingMenu() {
      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.display(fakeUuidBuilder.createFixedUUID()));
    }

    @Test
    @DisplayName("`사장님`이 `메뉴`를 `숨김 상태(Hide)` 할 수 있다.")
    void hideMenu() {

      Product productUdon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));
      Menu udonMenu = menuService.create(MenuFixture.createMenu(fakeUuidBuilder, TWO_UDONS, THRITY_THOUSANDS, menuGroup, productUdon, 2));

      Menu displayed = menuService.hide(udonMenu.getId());

      assertThat(displayed.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("`사장님`이 `메뉴`를 `공개 상태(Visible)` 할 수 있다.")
    void showMenu() {

      Product productUdon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));
      Menu udonMenu = menuService.create(MenuFixture.createMenu(fakeUuidBuilder, TWO_UDONS, THRITY_THOUSANDS, menuGroup, productUdon, 2));

      Menu displayed = menuService.display(udonMenu.getId());

      assertThat(displayed.isDisplayed()).isTrue();

    }
  }
}
