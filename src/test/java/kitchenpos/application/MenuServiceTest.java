package kitchenpos.application;

import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
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

  /*@Test
  @DisplayName("메뉴를 등록하기 위해 메뉴 그룹, 이름을 입력해야 한다.")
  void case1() {

    final MenuProductsFixture menuProductsFixture =
        new MenuProductsFixture(
            new MenuProductFixture(new ProductFixture("김치찌개", BigDecimal.valueOf(20_000L)), 1),
            new MenuProductFixture(new ProductFixture("된장찌개", BigDecimal.valueOf(15_000L)), 1),
            new MenuProductFixture(new ProductFixture("부대찌개", BigDecimal.valueOf(10_000L)), 1));

    final MenuGroupFixture hotMenu1 = new MenuGroupFixture("추천메뉴1");
    final MenuFixture gookSetMenu =
        new MenuFixture("찌개 세트 메뉴", BigDecimal.valueOf(45_000L), hotMenu1, menuProductsFixture);

    given(menuGroupRepository.findById(any()))
        .willReturn(Optional.ofNullable(hotMenu1.getMenuGroup()));
    given(productRepository.findAllByIdIn(any()))
        .willReturn(
            menuProductsFixture.getMenuProductList().stream()
                .map(MenuProduct::getProduct)
                .toList());

    for (MenuProduct menuProduct : menuProductsFixture.getMenuProductList()) {
      given(productRepository.findById(menuProduct.getProductId()))
          .willReturn(Optional.ofNullable(menuProduct.getProduct()));
    }
    given(purgomalumClient.containsProfanity(any())).willReturn(false);
    given(menuRepository.save(any())).willReturn(gookSetMenu.getMenu());

    Menu menu = menuService.create(gookSetMenu.getMenu());

    Assertions.assertThat(gookSetMenu.getMenu().getPrice()).isEqualTo(menu.getPrice());
  }

  @Test
  @DisplayName("메뉴를 등록하기 위해 메뉴 이름에 비속어 또는 욕설을 작성할 수 없다.")
  void case2() {

    final MenuProductsFixture menuProductsFixture =
        new MenuProductsFixture(
            new MenuProductFixture(new ProductFixture("김치찌개", BigDecimal.valueOf(20_000L)), 1),
            new MenuProductFixture(new ProductFixture("된장찌개", BigDecimal.valueOf(15_000L)), 1),
            new MenuProductFixture(new ProductFixture("부대찌개", BigDecimal.valueOf(10_000L)), 1));

    final MenuGroupFixture hotMenu1 = new MenuGroupFixture("추천메뉴1");
    final MenuFixture gookSetMenu =
        new MenuFixture("찌개 세트 메뉴", BigDecimal.valueOf(45_000L), hotMenu1, menuProductsFixture);

    given(menuGroupRepository.findById(any()))
        .willReturn(Optional.ofNullable(hotMenu1.getMenuGroup()));
    given(productRepository.findAllByIdIn(any()))
        .willReturn(
            menuProductsFixture.getMenuProductList().stream()
                .map(MenuProduct::getProduct)
                .toList());

    for (MenuProduct menuProduct : menuProductsFixture.getMenuProductList()) {
      given(productRepository.findById(menuProduct.getProductId()))
          .willReturn(Optional.ofNullable(menuProduct.getProduct()));
    }
    given(purgomalumClient.containsProfanity(any())).willReturn(true);

    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(() -> menuService.create(gookSetMenu.getMenu()));
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(longs = {-1})
  @DisplayName("메뉴를 등록하기 위해 금액을 입력하지 않았거나 또는 0원 밑으로 입력할 수 없다.")
  void case3(Long value) {

    final MenuProductsFixture menuProductsFixture =
        new MenuProductsFixture(
            new MenuProductFixture(new ProductFixture("김치찌개", BigDecimal.valueOf(20_000L)), 1),
            new MenuProductFixture(new ProductFixture("된장찌개", BigDecimal.valueOf(15_000L)), 1),
            new MenuProductFixture(new ProductFixture("부대찌개", BigDecimal.valueOf(10_000L)), 1));

    final MenuGroupFixture hotMenu1 = new MenuGroupFixture("추천메뉴1");
    final MenuFixture gookSetMenu =
        new MenuFixture(
            "찌개 세트 메뉴",
            Objects.isNull(value) ? null : BigDecimal.valueOf(value),
            hotMenu1,
            menuProductsFixture);

    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(() -> menuService.create(gookSetMenu.getMenu()));
  }

  @ParameterizedTest
  @ValueSource(ints = {-1})
  @DisplayName("메뉴를 등록하기 위해 상품이 있어야하고, 수량을 0개 밑으로 입력할 수 없다.")
  void case4(Integer value) {

    final MenuProductsFixture menuProductsFixture =
            new MenuProductsFixture(
                    new MenuProductFixture(new ProductFixture("김치찌개", BigDecimal.valueOf(20_000L)), value),
                    new MenuProductFixture(new ProductFixture("된장찌개", BigDecimal.valueOf(15_000L)), value),
                    new MenuProductFixture(new ProductFixture("부대찌개", BigDecimal.valueOf(10_000L)), value));

    final MenuGroupFixture hotMenu1 = new MenuGroupFixture("추천메뉴1");
    final MenuFixture gookSetMenu =
            new MenuFixture("찌개 세트 메뉴", BigDecimal.valueOf(45_000L), hotMenu1, menuProductsFixture);

    given(menuGroupRepository.findById(any()))
            .willReturn(Optional.ofNullable(hotMenu1.getMenuGroup()));
    given(productRepository.findAllByIdIn(any()))
            .willReturn(
                    menuProductsFixture.getMenuProductList().stream()
                            .map(MenuProduct::getProduct)
                            .toList());

    Assertions.assertThatIllegalArgumentException()
            .isThrownBy(() -> menuService.create(gookSetMenu.getMenu()));

  }

  @Test
  @DisplayName("메뉴를 등록하기 위해 해당 상품 정보가 상품에 존재해야 한다.")
  void case5() {

    final MenuProductsFixture menuProductsFixture =
            new MenuProductsFixture(
                    new MenuProductFixture(new ProductFixture("김치찌개", BigDecimal.valueOf(20_000L)), 1),
                    new MenuProductFixture(new ProductFixture("된장찌개", BigDecimal.valueOf(15_000L)), 1),
                    new MenuProductFixture(new ProductFixture("부대찌개", BigDecimal.valueOf(10_000L)), 1));

    final MenuGroupFixture hotMenu1 = new MenuGroupFixture("추천메뉴1");
    final MenuFixture gookSetMenu =
            new MenuFixture("찌개 세트 메뉴", BigDecimal.valueOf(45_000L), hotMenu1, menuProductsFixture);

    given(menuGroupRepository.findById(any()))
            .willReturn(Optional.ofNullable(hotMenu1.getMenuGroup()));
    given(productRepository.findAllByIdIn(any()))
            .willReturn(
                    menuProductsFixture.getMenuProductList().stream()
                            .map(MenuProduct::getProduct)
                            .toList());

    Assertions.assertThatRuntimeException()
            .isThrownBy(() -> menuService.create(gookSetMenu.getMenu()));
  }*/

  @Test
  @DisplayName("메뉴를 등록할 때 동일한 상품을 등록할 수 없다.")
  void case6() {}

  @Test
  @DisplayName("메뉴 금액이 상품 금액을 수량 만큼 합산한 금액 보다 클 수 없다.")
  void case7() {}

  @Test
  @DisplayName("메뉴를 등록하면 해당 상품이 공개된다.")
  void case8() {}

  @Test
  @DisplayName("메뉴의 금액을 변경하기 위해 해당 메뉴가 존재해야 하고, 변경할 금액을 입력해야 한다.")
  void case9() {}

  @Test
  @DisplayName("메뉴의 금액을 변경하기 위해 금액을 입력하지 않았거나 또는 0원 밑으로 입력할 수 없다.")
  void case10() {}

  @Test
  @DisplayName("메뉴의 금액을 변경하기 위해 변경할 금액이 상품 금액을 수량 만큼 합산한 금액 보다 클 수 없다.")
  void case11() {}

  @Test
  @DisplayName("메뉴를 노출하기 위해 메뉴가 존재해야한다.")
  void case12() {}

  @Test
  @DisplayName("메뉴를 노출하기 위해 메뉴 금액이 상품 금액을 수량 만큼 합산한 금액 보다 클 수 없다.")
  void case13() {}

  @Test
  @DisplayName("메뉴를 숨기기 위해 메뉴가 존재해야 한다.")
  void case14() {}

  @Test
  @DisplayName("메뉴를 전체 조회할 수 있다.")
  void case15() {}
}
