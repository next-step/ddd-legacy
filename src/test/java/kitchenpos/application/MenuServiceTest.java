package kitchenpos.application;

import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.*;
import kitchenpos.fixtures.*;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.infra.menu.InMemoryMenuGroupRepository;
import kitchenpos.infra.menu.InMemoryMenuRepository;
import kitchenpos.infra.product.InMemoryProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

  private final MenuRepository menuRepository = new InMemoryMenuRepository();
  private final MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
  private final ProductRepository productRepository = new InMemoryProductRepository();
  private MenuService menuService;
  @Mock private PurgomalumClient purgomalumClient;

  @BeforeEach
  void beforeEach() {
    this.menuService =
        new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
  }

  @Nested
  @DisplayName("메뉴 등록")
  class Nested1 {

    @Test
    @DisplayName("메뉴를 등록하기 위해 메뉴 그룹, 이름을 입력해야 한다.")
    void case1() {
      final Menu menu = FixtureMenu.fixtureMenu();
      final Product product = menu.getMenuProducts().getFirst().getProduct();
      final MenuGroup menuGroup = menu.getMenuGroup();

      menu.setName("닭다리");

      menuRepository.save(menu);
      productRepository.save(product);
      menuGroupRepository.save(menuGroup);

      given(purgomalumClient.containsProfanity(any())).willReturn(false);

      menuService.create(menu);
      Assertions.assertThat(menu.getName()).isEqualTo("닭다리");
    }

    @Test
    @DisplayName("메뉴를 등록하기 위해 메뉴 이름에 비속어 또는 욕설을 작성할 수 없다.")
    void case2() {

      final Menu menu = FixtureMenu.fixtureMenu();
      final Product product = menu.getMenuProducts().getFirst().getProduct();
      final MenuGroup menuGroup = menu.getMenuGroup();

      menuRepository.save(menu);
      productRepository.save(product);
      menuGroupRepository.save(menuGroup);

      given(purgomalumClient.containsProfanity(any())).willReturn(true);

      Assertions.assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
    }

    @Test
    @DisplayName("메뉴를 등록하기 위해 금액을 입력하지 않았다.")
    void case3() {
      final Menu menu = FixtureMenu.fixtureMenu();

      menu.setName("닭다리");
      menu.setPrice(null);

      Assertions.assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
    }

    @Test
    @DisplayName("메뉴를 등록하기 위해 금액은 음수로 입력할 수 없다.")
    void case4() {
      final Menu menu = FixtureMenu.fixtureMenu();

      menu.setName("닭다리");
      menu.setPrice(BigDecimal.valueOf(-1));

      Assertions.assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1})
    @DisplayName("메뉴를 등록하기 위해 상품이 있어야하고, 수량을 0개 밑으로 입력할 수 없다.")
    void case5(Integer value) {
      final Menu menu = FixtureMenu.fixtureMenu();
      final Product product = menu.getMenuProducts().getFirst().getProduct();
      final MenuGroup menuGroup = menu.getMenuGroup();

      menuRepository.save(menu);
      productRepository.save(product);
      menuGroupRepository.save(menuGroup);

      final MenuProduct menuProduct = FixtureProduct.fixtureMenuProduct();
      menuProduct.setQuantity(value);
      menu.setMenuProducts(List.of(menuProduct));

      Assertions.assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
    }

    @Test
    @DisplayName("메뉴를 등록하기 위해 해당 상품 정보가 상품에 존재해야 한다.")
    void case6() {
      final Menu menu = FixtureMenu.fixtureMenu();
      final Product product = menu.getMenuProducts().getFirst().getProduct();
      final MenuGroup menuGroup = menu.getMenuGroup();

      menuRepository.save(menu);
      productRepository.save(product);
      menuGroupRepository.save(menuGroup);

      final Menu request = FixtureMenu.fixtureMenu();

      Assertions.assertThatException().isThrownBy(() -> menuService.create(request));
    }

    @Test
    @DisplayName("메뉴 금액이 상품 금액을 수량 만큼 합산한 금액 보다 클 수 없다.")
    void case7() {
      final Menu menu = FixtureMenu.fixtureMenu();
      final Product product = menu.getMenuProducts().getFirst().getProduct();
      final MenuGroup menuGroup = menu.getMenuGroup();

      menuRepository.save(menu);
      productRepository.save(product);
      menuGroupRepository.save(menuGroup);

      menu.setPrice(BigDecimal.valueOf(30_000_000L));

      Assertions.assertThatIllegalArgumentException().isThrownBy(() -> menuService.create(menu));
    }

    @Test
    @DisplayName("메뉴를 등록하면 해당 상품이 공개된다.")
    void case8() {
      final Menu menu = FixtureMenu.fixtureMenu();
      final Product product = menu.getMenuProducts().getFirst().getProduct();
      final MenuGroup menuGroup = menu.getMenuGroup();

      menuRepository.save(menu);
      productRepository.save(product);
      menuGroupRepository.save(menuGroup);

      given(purgomalumClient.containsProfanity(any())).willReturn(false);

      final Menu actual = menuService.create(menu);
      Assertions.assertThat(actual.isDisplayed()).isTrue();
    }
  }

  @Nested
  @DisplayName("메뉴 금액 변경")
  class Nested2 {
    @Test
    @DisplayName("메뉴의 금액을 변경하기 위해 해당 메뉴가 존재해야 하고, 변경할 금액을 입력해야 한다.")
    void case9() {
      final Menu menu = FixtureMenu.fixtureMenu();
      final Menu changeMenuPrice = FixtureMenu.fixtureMenu();

      menuRepository.save(menu);
      changeMenuPrice.setPrice(BigDecimal.valueOf(25_000L));

      final Menu actual = menuService.changePrice(menu.getId(), changeMenuPrice);
      Assertions.assertThat(actual.getPrice()).isEqualTo(menu.getPrice());
    }

    @Test
    @DisplayName("메뉴의 금액을 변경하기 위해 금액을 입력해야 한다.")
    void case10() {
      final Menu menu = FixtureMenu.fixtureMenu();
      final Menu changeMenuPrice = FixtureMenu.fixtureMenu();

      menuRepository.save(menu);

      changeMenuPrice.setPrice(null);
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(() -> menuService.changePrice(menu.getId(), changeMenuPrice));
    }

    @Test
    @DisplayName("메뉴의 금액을 변경하기 위해 금액을 0원 밑으로 입력할 수 없다.")
    void case11() {
      final Menu menu = FixtureMenu.fixtureMenu();
      final Menu changeMenuPrice = FixtureMenu.fixtureMenu();

      menuRepository.save(menu);

      changeMenuPrice.setPrice(BigDecimal.valueOf(-1));
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(() -> menuService.changePrice(menu.getId(), changeMenuPrice));
    }

    @Test
    @DisplayName("메뉴의 금액을 변경하기 위해 변경할 금액이 상품 금액을 수량 만큼 합산한 금액 보다 클 수 없다.")
    void case12() {
      final Menu menu = FixtureMenu.fixtureMenu();
      final Menu changeMenuPrice = FixtureMenu.fixtureMenu();

      menuRepository.save(menu);

      changeMenuPrice.setPrice(BigDecimal.valueOf(30_000L));
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(() -> menuService.changePrice(menu.getId(), changeMenuPrice));
    }
  }

  @Nested
  @DisplayName("메뉴 노출")
  class Nested3 {

    @Test
    @DisplayName("메뉴를 노출하기 위해 메뉴가 존재해야한다.")
    void case12() {
      final Menu menu = FixtureMenu.fixtureMenu();
      menu.setDisplayed(false);
      menuRepository.save(menu);

      final Menu actual = menuService.display(menu.getId());
      Assertions.assertThat(actual.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("메뉴를 노출하기 위해 메뉴 금액이 상품 금액을 수량 만큼 합산한 금액 보다 클 수 없다.")
    void case13() {
      final Menu menu = FixtureMenu.fixtureMenu();

      menu.setDisplayed(false);
      menu.setPrice(BigDecimal.valueOf(30_000L));
      menuRepository.save(menu);

      Assertions.assertThatIllegalStateException()
          .isThrownBy(() -> menuService.display(menu.getId()));
    }

    @Test
    @DisplayName("메뉴를 숨기기 위해 메뉴가 존재해야 한다.")
    void case14() {
      final Menu menu = FixtureMenu.fixtureMenu();

      menu.setDisplayed(true);
      menuRepository.save(menu);

      final Menu actual = menuService.hide(menu.getId());
      Assertions.assertThat(actual.isDisplayed()).isFalse();
    }
  }

  @Nested
  @DisplayName("메뉴 전체 조회")
  class Nested4 {
    @Test
    @DisplayName("메뉴를 전체 조회할 수 있다.")
    void case15() {
      final Menu menu = FixtureMenu.fixtureMenu();
      menuRepository.save(menu);

      final List<Menu> all = menuService.findAll();
      Assertions.assertThat(all).isNotEmpty();
    }
  }
}
