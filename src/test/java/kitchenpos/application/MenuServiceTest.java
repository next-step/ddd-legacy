package kitchenpos.application;

import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import kitchenpos.domain.*;
import kitchenpos.fixtures.*;
import kitchenpos.infra.PurgomalumClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
  @Mock private MenuRepository menuRepository;
  @Mock private MenuGroupRepository menuGroupRepository;
  @Mock private ProductRepository productRepository;
  @Mock private PurgomalumClient purgomalumClient;

  @InjectMocks private MenuService menuService;

  @Test
  @DisplayName("메뉴를 등록하기 위해 메뉴 그룹, 이름을 입력해야 한다.")
  void case1() {
    final Menu menu = FixtureMenu.fixtureMenu();
    final Product product = FixtureProduct.fixtureProduct();
    final MenuGroup menuGroup = FixtureMenu.fixtureMenuGroup();

    menu.setName("닭다리");

    given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
    given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
    given(productRepository.findById(any())).willReturn(Optional.of(product));
    given(purgomalumClient.containsProfanity(any())).willReturn(false);
    given(menuRepository.save(any())).willReturn(menu);

    menuService.create(menu);
    Assertions.assertThat(menu.getName()).isEqualTo("닭다리");
  }

  @Test
  @DisplayName("메뉴를 등록하기 위해 메뉴 이름에 비속어 또는 욕설을 작성할 수 없다.")
  void case2() {

    final Menu menu = FixtureProduct.fixtureMenu();
    final Product product = FixtureProduct.fixtureProduct();
    final MenuGroup menuGroup = FixtureMenu.fixtureMenuGroup();

    given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
    given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));

    given(productRepository.findById(any())).willReturn(Optional.of(product));
    given(purgomalumClient.containsProfanity(any())).willReturn(true);

    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(() -> menuService.create(menu));
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(longs = {-1})
  @DisplayName("메뉴를 등록하기 위해 금액을 입력하지 않았거나 또는 0원 밑으로 입력할 수 없다.")
  void case3(Long value) {
    final Menu menu = FixtureMenu.fixtureMenu();

    menu.setName("닭다리");
    menu.setPrice(value == null ? null : BigDecimal.valueOf(value));

    Assertions.assertThatIllegalArgumentException()
            .isThrownBy(() -> menuService.create(menu));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1})
  @DisplayName("메뉴를 등록하기 위해 상품이 있어야하고, 수량을 0개 밑으로 입력할 수 없다.")
  void case4(Integer value) {
    final Menu menu = FixtureMenu.fixtureMenu();
    final Product product = FixtureProduct.fixtureProduct();
    final MenuGroup menuGroup = FixtureMenu.fixtureMenuGroup();
    final MenuProduct menuProduct = FixtureProduct.fixtureMenuProduct();

    menuProduct.setQuantity(value);
    menu.setMenuProducts(List.of(menuProduct));

    given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
    given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));

    Assertions.assertThatIllegalArgumentException()
            .isThrownBy(() -> menuService.create(menu));
  }

  @Test
  @DisplayName("메뉴를 등록하기 위해 해당 상품 정보가 상품에 존재해야 한다.")
  void case5() {
    final Menu menu = FixtureMenu.fixtureMenu();
    final Product product = FixtureProduct.fixtureProduct();
    final MenuGroup menuGroup = FixtureMenu.fixtureMenuGroup();

    given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
    given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));

    final Product failProduct = FixtureProduct.fixtureProduct();
    failProduct.setId(UUID.randomUUID());
    given(productRepository.findById(any())).willReturn(null);

    Assertions.assertThatException()
            .isThrownBy(() -> menuService.create(menu));
  }

  @Test
  @DisplayName("메뉴 금액이 상품 금액을 수량 만큼 합산한 금액 보다 클 수 없다.")
  void case7() {
    final Menu menu = FixtureMenu.fixtureMenu();
    final Product product = FixtureProduct.fixtureProduct();
    final MenuGroup menuGroup = FixtureMenu.fixtureMenuGroup();

    menu.setPrice(BigDecimal.valueOf(30_000_000L));

    given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
    given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));

    given(productRepository.findById(any())).willReturn(Optional.of(product));

    Assertions.assertThatIllegalArgumentException()
            .isThrownBy(() -> menuService.create(menu));
  }

  @Test
  @DisplayName("메뉴를 등록하면 해당 상품이 공개된다.")
  void case8() {
    final Menu menu = FixtureMenu.fixtureMenu();
    final Product product = FixtureProduct.fixtureProduct();
    final MenuGroup menuGroup = FixtureMenu.fixtureMenuGroup();

    given(menuGroupRepository.findById(any())).willReturn(Optional.of(menuGroup));
    given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
    given(productRepository.findById(any())).willReturn(Optional.of(product));
    given(purgomalumClient.containsProfanity(any())).willReturn(false);
    given(menuRepository.save(any())).willReturn(menu);

    final Menu actual = menuService.create(menu);
    Assertions.assertThat(actual.isDisplayed()).isTrue();
  }

  @Test
  @DisplayName("메뉴의 금액을 변경하기 위해 해당 메뉴가 존재해야 하고, 변경할 금액을 입력해야 한다.")
  void case9() {
    final Menu menu = FixtureMenu.fixtureMenu();
    final Menu changeMenuPrice = FixtureMenu.fixtureMenu();

    given(menuRepository.findById(any())).willReturn(Optional.of(menu));

    changeMenuPrice.setPrice(BigDecimal.valueOf(25_000L));
    final Menu actual = menuService.changePrice(menu.getId(), changeMenuPrice);
    Assertions.assertThat(actual.getPrice()).isEqualTo(menu.getPrice());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(longs = {-1})
  @DisplayName("메뉴의 금액을 변경하기 위해 금액을 입력하지 않았거나 또는 0원 밑으로 입력할 수 없다.")
  void case10(Long value) {
    final Menu menu = FixtureMenu.fixtureMenu();
    final Menu changeMenuPrice = FixtureMenu.fixtureMenu();

    changeMenuPrice.setPrice(value == null ? null : BigDecimal.valueOf(value));
    Assertions.assertThatIllegalArgumentException()
                    .isThrownBy(() -> menuService.changePrice(menu.getId(), changeMenuPrice));
  }

  @Test
  @DisplayName("메뉴의 금액을 변경하기 위해 변경할 금액이 상품 금액을 수량 만큼 합산한 금액 보다 클 수 없다.")
  void case11() {
    final Menu menu = FixtureMenu.fixtureMenu();
    final Menu changeMenuPrice = FixtureMenu.fixtureMenu();

    given(menuRepository.findById(any())).willReturn(Optional.of(menu));

    changeMenuPrice.setPrice(BigDecimal.valueOf(30_000L));
    Assertions.assertThatIllegalArgumentException()
            .isThrownBy(() -> menuService.changePrice(menu.getId(), changeMenuPrice));
  }

  @Test
  @DisplayName("메뉴를 노출하기 위해 메뉴가 존재해야한다.")
  void case12() {
    final Menu menu = FixtureMenu.fixtureMenu();

    menu.setDisplayed(false);
    given(menuRepository.findById(any())).willReturn(Optional.of(menu));

    final Menu actual = menuService.display(menu.getId());
    Assertions.assertThat(actual.isDisplayed()).isTrue();
  }

  @Test
  @DisplayName("메뉴를 노출하기 위해 메뉴 금액이 상품 금액을 수량 만큼 합산한 금액 보다 클 수 없다.")
  void case13() {
    final Menu menu = FixtureMenu.fixtureMenu();

    menu.setDisplayed(false);
    menu.setPrice(BigDecimal.valueOf(30_000L));
    given(menuRepository.findById(any())).willReturn(Optional.of(menu));

    Assertions.assertThatIllegalStateException()
            .isThrownBy(() -> menuService.display(menu.getId()));
  }

  @Test
  @DisplayName("메뉴를 숨기기 위해 메뉴가 존재해야 한다.")
  void case14() {
    final Menu menu = FixtureMenu.fixtureMenu();

    menu.setDisplayed(true);
    given(menuRepository.findById(any())).willReturn(Optional.of(menu));

    final Menu actual = menuService.hide(menu.getId());
    Assertions.assertThat(actual.isDisplayed()).isFalse();
  }

  @Test
  @DisplayName("메뉴를 전체 조회할 수 있다.")
  void case15() {
    final Menu menu = FixtureMenu.fixtureMenu();
    given(menuRepository.findAll()).willReturn(List.of(menu));

    final List<Menu> all = menuService.findAll();
    Assertions.assertThat(all).isNotEmpty();
  }
}
