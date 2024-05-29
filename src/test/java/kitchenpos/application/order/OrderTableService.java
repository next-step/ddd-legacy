package kitchenpos.application.order;

import kitchenpos.application.MenuService;
import kitchenpos.application.product.FakeUuidBuilder;
import kitchenpos.domain.*;
import kitchenpos.domain.menu.FakeMenuGroupRepository;
import kitchenpos.domain.menu.FakeMenuRepository;
import kitchenpos.domain.product.FakeProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTableService {
  public static final int TEN_THOUSAND = 10_000;
  public static final long TWENTY_THOUSANDS = 20_000L;
  public static final int THRITY_THOUSANDS = 30_000;
  public static final int FIFTY_THOUSANDS = 50_000;
  public static final String UDON = "우동";
  public static final String RAMEN = "라면";
  public static final String FOR_TWO = "이인용";
  public static final String TWO_UDONS = "우동과라면";

  private MenuGroupRepository menuGroupRepository;
  private MenuRepository menuRepository;
  private MenuService menuService;
  private ProductRepository productRepository;
  private PurgomalumClient purgomalumClient;
  private FakeUuidBuilder fakeUuidBuilder;

  private Product udon;
  private Product ramen;
  private MenuGroup menuGroup;
  @BeforeEach
  void setUp() {
    menuRepository = new FakeMenuRepository();
    menuGroupRepository = new FakeMenuGroupRepository();
    purgomalumClient = new FakePurgomalumClient();
    productRepository = new FakeProductRepository();
    fakeUuidBuilder = new FakeUuidBuilder();

    menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

    udon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));
    ramen = productRepository.save(ProductFixture.createProduct(RAMEN, TEN_THOUSAND, fakeUuidBuilder));

    menuGroup = MenuGroupFixture.createMenuGroup(FOR_TWO);
    menuGroupRepository.save(menuGroup);
  }

  @Nested
  @DisplayName("주문 테이블을 등록한다.")
  class OrderTableRegistration {
    @Test
    @DisplayName("주문 테이블을 등록한다.")
    void createOrderTable() {
      Menu actual = menuService.create(MenuFixture.createMenu(TWO_UDONS, TWENTY_THOUSANDS, menuGroup, udon, 2));

      assertAll(
              () -> assertThat(actual.getName()).isEqualTo(TWO_UDONS),
              () -> assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(TWENTY_THOUSANDS))
      );
    }

    @DisplayName("주문 테이블의 이름은 비어있을 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {""})
    void failToCreateMenuWithEmptyString(final String name) {

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.create(MenuFixture.createMenu(name, THRITY_THOUSANDS, menuGroup, udon, 2)));
    }

    @DisplayName("`메뉴 가격`은 양수이어야한다.")
    @ParameterizedTest
    @ValueSource(ints = {-324234, -234, 0})
    void failToCreateProductWithProfanityAndEmptyProductName(final int price) {

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.create(MenuFixture.createMenu(TWO_UDONS, THRITY_THOUSANDS, menuGroup, udon, 2)));
    }

    @DisplayName("`메뉴`는 `상품`들(1개 이상)을 가진다.")
    void faileToCreateWithZeroProduct() {

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.create(MenuFixture.createMenu(TWO_UDONS, THRITY_THOUSANDS, menuGroup, udon, 0)));
    }

    @DisplayName("`메뉴`는 `메뉴상품`들의 총액을 넘을 수 없다.")
    void failToCreateWithHigherPriceThanMenuProductsPrices() {

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.create(MenuFixture.createMenu(TWO_UDONS, FIFTY_THOUSANDS, menuGroup, udon, 2)));
    }
  }
  @Nested
  @DisplayName("주문 테이블들을 조회할 수 있다.")
  class MenuCateogryRegistration {
    @Test
    @DisplayName("사장님이 주문 테이블들을 조회할 수 있다.")
    void createProduct() {

      Menu udonMenu = menuService.create(MenuFixture.createMenu(TWO_UDONS, TWENTY_THOUSANDS, menuGroup, udon, 2));

      List<Menu> actual = menuService.findAll();

      assertThat(actual).contains(udonMenu);
    }

  }
  @Nested
  @DisplayName("주문 테이블에 손님을 등록한다.")
  class MenuCateogryRegistration {
    @Test
    @DisplayName("주문 테이블의 변경하고자 하는 손님의 수가 음수라면 변경되지 않는다.")
    void createProduct() {

      Menu udonMenu = menuService.create(MenuFixture.createMenu(TWO_UDONS, TWENTY_THOUSANDS, menuGroup, udon, 2));

      List<Menu> actual = menuService.findAll();

      assertThat(actual).contains(udonMenu);
    }

  }

  @Nested
  @DisplayName("주문 테이블의 손님의 수를 변경한다.")
  class MenuPriceChange {
    @Test
    @DisplayName("주문 테이블의 변경하고자 하는 손님의 수가 음수라면 변경되지 않는다.")
    void changeMenuPriceWithHigherPriceThanMenuProductsPrices() {
      Menu menu = menuService.create(MenuFixture.createMenu(fakeUuidBuilder, TWO_UDONS, TWENTY_THOUSANDS, menuGroup, udon, 2));

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.changePrice(menu.getId(), MenuFixture.createMenu(TWO_UDONS, FIFTY_THOUSANDS, menuGroup, udon, 2)));
    }

    @DisplayName("주문 테이블이 등록되어 있지 않으면 변경되지 않는다.")
    @ParameterizedTest
    @ValueSource(ints = {-324234, -234, -1})
    void changeMenuPrice(int price) {
      Menu menu = menuService.create(MenuFixture.createMenu(fakeUuidBuilder, TWO_UDONS, TWENTY_THOUSANDS, menuGroup, udon, 2));

      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.changePrice(menu.getId(), MenuFixture.createMenu(TWO_UDONS, price, menuGroup, udon, 2)));
    }
  }

  @Nested
  @DisplayName("손님의 수를 초기화하여(0이 기본값) 주문 테이블의 상태를 `미사용중`으로 변경할 수 있다.")
  class MenuDisplayStatus {
    @DisplayName("주문 테이블의 주문 상태가 `주문완료`이어야 한다.")
    @ParameterizedTest
    @ValueSource(longs = {10000000, 41000, 40001})
    void changeDisplayStatusWithHigherPriceThanMenuProducts(long price) {

      udon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));
      Menu udonMenu = menuService.create(MenuFixture.createMenu(fakeUuidBuilder, TWO_UDONS, THRITY_THOUSANDS, menuGroup, udon, 2));


      assertThatIllegalArgumentException()
              .isThrownBy(() ->
                      menuService.changePrice(udonMenu.getId(),
                              MenuFixture.createMenu(fakeUuidBuilder, TWO_UDONS, price, menuGroup, udon, 2)));
    }

    @DisplayName("주문 테이블이 등록되어 있지 않으면 변경되지 않는다.")
    void showMenuWithNotExistingMenu() {
      assertThatIllegalArgumentException()
              .isThrownBy(() -> menuService.display(fakeUuidBuilder.createFixedUUID()));
    }

    @Test
    @DisplayName("주문 테이블의 상태가 `사용 중`이 아니라면 변경되지 않는다.")
    void hideMenu() {

      udon = productRepository.save(ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));
      Menu udonMenu = menuService.create(MenuFixture.createMenu(fakeUuidBuilder, TWO_UDONS, THRITY_THOUSANDS, menuGroup, udon, 2));

      Menu displayed = menuService.hide(udonMenu.getId());

      assertThat(displayed.isDisplayed()).isFalse();
    }

  }
}
