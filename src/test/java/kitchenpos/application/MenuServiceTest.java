package kitchenpos.application;

import kitchenpos.Fixtures;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.Fixtures.createMenu_두마리_치킨;
import static kitchenpos.Fixtures.메뉴그룹_생성;
import static kitchenpos.Fixtures.메뉴_상품_생성;
import static kitchenpos.Fixtures.상품_생성;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("메뉴 서비스 테스트")
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @InjectMocks
    private MenuService menuService;

    private MenuGroup menuGroup;

    private Product product;

    @BeforeEach
    void setUp() {
        product = 상품_생성("후라이드", 16_000L);
        menuGroup = 메뉴그룹_생성("치킨 세트");
    }

    @Test
    @DisplayName("메뉴를 등록 할 수 있다")
    void saveMenu() {
        // given
        given(menuGroupRepository.findById(any())).willReturn(java.util.Optional.of(menuGroup));
        given(productRepository.findById(any())).willReturn(java.util.Optional.of(product));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(menuRepository.save(any())).willReturn(Fixtures.메뉴_생성_두마리_치킨());

        // when
        Menu 응답_결과 = menuService.create(Fixtures.메뉴_생성_두마리_치킨());

        // then
        assertThat(응답_결과).isNotNull();
    }

    @Test
    @DisplayName("메뉴는 메뉴 그룹에 속해야 한다.")
    void requireMenuGroup() {
        // given
        Menu 요청_객체 = createMenu_두마리_치킨(BigDecimal.valueOf(16_000L), null, menuGroup, List.of(메뉴_상품_생성(product, 1L, 1)), "저녁 안주", true);

        // when & then
        assertThatThrownBy(() -> menuService.create(요청_객체))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("메뉴는 적어도 하나의 메뉴 항목을 포함해야 한다.")
    void requireMenuProduct() {
        // given
        given(menuGroupRepository.findById(any())).willReturn(java.util.Optional.of(menuGroup));
        Menu 요청_객체 = createMenu_두마리_치킨(BigDecimal.valueOf(16_000L), menuGroup.getId(), menuGroup, null, "저녁 안주", true);

        // when & then
        assertThatThrownBy(() -> menuService.create(요청_객체))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 가격은 0원 이상이어야 한다")
    void requireMenuPrice() {
        // given
        Menu 요청_객체 = createMenu_두마리_치킨(new BigDecimal("-1"), menuGroup.getId(), menuGroup, List.of(메뉴_상품_생성(product, 1L, 1)), "저녁 안주", true);

        // when & then
        assertThatThrownBy(() -> menuService.create(요청_객체))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("하나의 메뉴에 여러 제품을 구성할 수 있다.")
    void requireMenuHaveMultiProducts() {
        // given
        given(menuGroupRepository.findById(any())).willReturn(java.util.Optional.of(menuGroup));
        given(productRepository.findAllByIdIn(any())).willReturn(Collections.emptyList());
        Menu 요청_객체 = createMenu_두마리_치킨(BigDecimal.valueOf(16_000L), menuGroup.getId(), menuGroup, List.of(new MenuProduct()), "저녁 안주", true);

        // when & then
        assertThatThrownBy(() -> menuService.create(요청_객체))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("제품의 갯수 또는 수량은 0보다 커야 한다.")
    void requireMenuProductCountOrQuantity() {
        // given
        given(menuGroupRepository.findById(any())).willReturn(java.util.Optional.of(menuGroup));
        given(productRepository.findById(any())).willReturn(java.util.Optional.of(product));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        Menu 요청_객체 = createMenu_두마리_치킨(BigDecimal.valueOf(16_000L), menuGroup.getId(), menuGroup, List.of(메뉴_상품_생성(product, 0L, 0)), "저녁 안주", true);

        // when & then
        assertThatThrownBy(() -> menuService.create(요청_객체))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴의 가격은 메뉴에 포함된 제품들의 가격 합계이다.")
    void requireMenuPriceIsSumOfMenuProductsPrice() {
        // given
        given(menuGroupRepository.findById(any())).willReturn(java.util.Optional.of(menuGroup));
        given(productRepository.findById(any())).willReturn(java.util.Optional.of(product));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        Menu 요청_객체 = createMenu_두마리_치킨(BigDecimal.valueOf(16_001L), menuGroup.getId(), menuGroup, List.of(메뉴_상품_생성(product, 1L, 1)), "저녁 안주", true);

        // when & then
        assertThatThrownBy(() -> menuService.create(요청_객체))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 이름은 비어 있거나 외설적인 내용을 포함할 수 없다.")
    @ValueSource(strings = {"짐승만도 못한 개123자식", "FUCKING"})
    @ParameterizedTest
    void requireMenuName(String name) {
        // given
        given(menuGroupRepository.findById(any())).willReturn(java.util.Optional.of(menuGroup));
        given(productRepository.findById(any())).willReturn(java.util.Optional.of(product));
        given(productRepository.findAllByIdIn(any())).willReturn(List.of(product));
        given(purgomalumClient.containsProfanity(any())).willReturn(true);
        Menu 요청_객체 = createMenu_두마리_치킨(BigDecimal.valueOf(16_000L), menuGroup.getId(), menuGroup, List.of(메뉴_상품_생성(product, 1L, 1)), name, true);

        // when & then
        assertThatThrownBy(() -> menuService.create(요청_객체))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("가격 값은 존재해야 하며 양수여야 한다.")
    void updateMenuPriceMustBePrice() {
        // given
        Menu 요청_객체 = createMenu_두마리_치킨(BigDecimal.valueOf(-1L), menuGroup.getId(), menuGroup, List.of(메뉴_상품_생성(product, 1L, 1)), "저녁 안주", true);

        // when & then
        assertThatThrownBy(() -> menuService.create(요청_객체))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하는 메뉴여야 가격을 변경 할 수 있다.")
    void requiredMustBeMenu() {
        // given
        final Menu 요청_객체 = Fixtures.메뉴_생성_두마리_치킨();
        // when & then
        assertThatThrownBy(() -> menuService.changePrice(요청_객체.getId(), 요청_객체))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("메뉴 가격이 상품 가격 * 수량의 총합보다 작거나 같지 않으면 가격을 변경 할 수 없다.")
    void updateMenuPriceIsNotLessThanSumOfMenuProductsPrice() {
        // given
        Menu 요청_객체 = createMenu_두마리_치킨(BigDecimal.valueOf(16_001L), menuGroup.getId(), menuGroup, List.of(메뉴_상품_생성(product, 1L, 1)), "저녁 안주", true);

        given(menuRepository.findById(any())).willReturn(Optional.of(요청_객체));

        // when & then
        assertThatThrownBy(() -> menuService.changePrice(요청_객체.getId(), 요청_객체))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 가격을 변경 할 수 있다.")
    void updateMenuPrice() {
        // given

        Menu 요청_객체 = Fixtures.메뉴_생성_두마리_치킨();
        given(menuRepository.findById(any())).willReturn(Optional.of(요청_객체));

        Menu 가격_올린_치킨_셋트 = createMenu_두마리_치킨(BigDecimal.valueOf(15_000L), menuGroup.getId(), menuGroup, List.of(메뉴_상품_생성(product, 1L, 1)), "저녁 안주", true);

        // when & then
        final Menu menu = menuService.changePrice(요청_객체.getId(), 가격_올린_치킨_셋트);

        assertThat(menu.getPrice()).isEqualTo(가격_올린_치킨_셋트.getPrice());
    }

    @Test
    @DisplayName("기존 메뉴만 노출 및 숨김 처리할 수 있다")
    void updateMenuDisplay() {
        // given
        Menu 요청_객체 = Fixtures.메뉴_생성_두마리_치킨();
        given(menuRepository.findById(any())).willReturn(Optional.of(요청_객체));

        // when
        final Menu menu = menuService.display(요청_객체.getId());

        // then
        assertThat(menu.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("메뉴의 가격이 메뉴에 포함된 제품들의 총 가격보다 큰 경우 노출 할 수 없다.")
    void updateMenuDisplayIsNotLessThanSumOfMenuProductsPrice() {
        // given
        Menu 요청_객체 = createMenu_두마리_치킨(BigDecimal.valueOf(16_001L), menuGroup.getId(), menuGroup, List.of(메뉴_상품_생성(product, 1L, 1)), "저녁 안주", true);

        given(menuRepository.findById(any())).willReturn(Optional.of(요청_객체));

        // when & then
        assertThatThrownBy(() -> menuService.display(요청_객체.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("메뉴 를 숨길수 있다.")
    void updateMenuHide() {
        // given
        Menu 요청_객체 = Fixtures.메뉴_생성_두마리_치킨();
        given(menuRepository.findById(any())).willReturn(Optional.of(요청_객체));

        // when
        final Menu menu = menuService.hide(요청_객체.getId());

        // then
        assertThat(menu.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("메뉴 목록을 조회 할 수 있다.")
    void findMenus() {
        // given
        Menu 요청_객체 = Fixtures.메뉴_생성_두마리_치킨();
        given(menuRepository.findAll()).willReturn(List.of(요청_객체));

        // when
        final List<Menu> menus = menuService.findAll();

        // then
        assertThat(menus).hasSize(1);
    }

}