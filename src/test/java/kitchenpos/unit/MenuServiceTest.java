package kitchenpos.unit;

import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.unit.fixture.MenuFixture.*;
import static kitchenpos.unit.fixture.ProductFixture.짜장면;
import static kitchenpos.unit.fixture.ProductFixture.탕수육;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("메뉴를 등록한다")
    @Test
    void create() {
        // given
        when(menuGroupRepository.findById(탕수육_세트.getId())).thenReturn(Optional.of(탕수육_세트));
        when(productRepository.findAllByIdIn(any(List.class))).thenReturn(한그릇_세트_상품목록);
        when(productRepository.findById(탕수육.getId())).thenReturn(Optional.of(탕수육));
        when(productRepository.findById(짜장면.getId())).thenReturn(Optional.of(짜장면));
        when(purgomalumClient.containsProfanity(한그릇_세트.getName())).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenReturn(한그릇_세트);

        // when
        Menu menu = menuService.create(한그릇_세트);

        // then
        assertThat(menu.getName()).isEqualTo(한그릇_세트.getName());
    }

    @DisplayName("메뉴의 가격이 null이 아니고, 0원 이상이어야 한다")
    @ParameterizedTest
    @ValueSource(strings = {"-1"})
    @NullSource
    void createInvalidPrice(BigDecimal price) {
        assertThatThrownBy(() -> menuService.create(createMenu(탕수육_세트, "한그릇 세트", price, 한그릇_세트_상품목록)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름이 null인 경우 등록되지 않는다")
    @ParameterizedTest
    @NullSource
    void createInvalidName(String name) {
        // given
        Menu menu = createMenu(탕수육_세트, name, BigDecimal.valueOf(14000), 한그릇_세트_상품목록);
        when(menuGroupRepository.findById(탕수육_세트.getId())).thenReturn(Optional.of(탕수육_세트));
        when(productRepository.findAllByIdIn(any(List.class))).thenReturn(한그릇_세트_상품목록);
        when(productRepository.findById(탕수육.getId())).thenReturn(Optional.of(탕수육));
        when(productRepository.findById(짜장면.getId())).thenReturn(Optional.of(짜장면));
        when(menuGroupRepository.findById(탕수육_세트.getId())).thenReturn(Optional.of(탕수육_세트));

        // when
        // then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹을 선택하지 않으면 등록되지 않는다")
    @Test
    void createNoMenuGroup() {
        // given
        when(menuGroupRepository.findById(탕수육_세트.getId())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> menuService.create(한그릇_세트))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("상품 목록을 선택하지 않으면 등록되지 않는다")
    @Test
    void createNoProductList() {
        // given
        when(menuGroupRepository.findById(탕수육_세트.getId())).thenReturn(Optional.of(탕수육_세트));
        when(productRepository.findAllByIdIn(any(List.class))).thenReturn(new ArrayList());

        // when
        // then
        assertThatThrownBy(() -> menuService.create(한그릇_세트))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 수량이 0개 이상이어야 한다")
    @Test
    void createInvalidProductQuantity() {
        // given
        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(createMenuProduct(탕수육, -1));
        Menu menu = createMenuWithMenuProducts(탕수육_세트, "한그릇 세트", BigDecimal.valueOf(14000), menuProducts);

        when(menuGroupRepository.findById(탕수육_세트.getId())).thenReturn(Optional.of(탕수육_세트));
        when(productRepository.findAllByIdIn(any(List.class))).thenReturn(한그릇_세트_상품목록);

        // when
        // then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 갖고 있는 상품 목록 가격의 합보다 크면 등록되지 않는다")
    @Test
    void creatBiggerThanProductsAmount() {
        // given
        Menu menu = createMenu(탕수육_세트, "한그릇 세트", BigDecimal.valueOf(99999), 한그릇_세트_상품목록);

        when(menuGroupRepository.findById(탕수육_세트.getId())).thenReturn(Optional.of(탕수육_세트));
        when(productRepository.findAllByIdIn(any(List.class))).thenReturn(한그릇_세트_상품목록);
        when(productRepository.findById(탕수육.getId())).thenReturn(Optional.of(탕수육));
        when(productRepository.findById(짜장면.getId())).thenReturn(Optional.of(짜장면));

        // when
        // then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격을 수정한다")
    @Test
    void changePrice() {
        // given
        Menu menu = createMenu(탕수육_세트, "한그릇 세트", BigDecimal.valueOf(13000), 한그릇_세트_상품목록);
        when(menuRepository.findById(한그릇_세트.getId())).thenReturn(Optional.of(한그릇_세트));

        // when
        Menu updateMenu = menuService.changePrice(한그릇_세트.getId(), menu);

        // then
        assertThat(updateMenu.getPrice()).isEqualTo(menu.getPrice());
    }

    @DisplayName("메뉴의 가격이 0원 이상이어야 한다")
    @Test
    void changeInvalidPrice() {
        // given
        Menu menu = createMenu(탕수육_세트, "한그릇 세트", BigDecimal.valueOf(-1), 한그릇_세트_상품목록);

        // when
        // then
        assertThatThrownBy(() -> menuService.changePrice(한그릇_세트.getId(), menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 갖고 있는 상품 목록 가격의 합보다 크면 수정되지 않는다")
    @Test
    void changePriceBiggerThanProductsAmount() {
        // given
        Menu menu = createMenu(탕수육_세트, "한그릇 세트", BigDecimal.valueOf(99999), 한그릇_세트_상품목록);
        when(menuRepository.findById(한그릇_세트.getId())).thenReturn(Optional.of(한그릇_세트));

        // when
        // then
        assertThatThrownBy(() -> menuService.changePrice(한그릇_세트.getId(), menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 비공개로 변경한다")
    @Test
    void hide() {
        // given
        when(menuRepository.findById(한그릇_세트.getId())).thenReturn(Optional.of(한그릇_세트));

        // when
        Menu hideMenu = menuService.hide(한그릇_세트.getId());

        // then
        assertThat(hideMenu.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴를 공개로 변경한다")
    @Test
    void display() {
        // given
        when(menuRepository.findById(한그릇_세트.getId())).thenReturn(Optional.of(한그릇_세트));
        menuService.hide(한그릇_세트.getId());

        // when
        Menu displayMenu = menuService.display(한그릇_세트.getId());

        // then
        assertThat(displayMenu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴의 가격이 갖고 있는 상품 목록 가격의 합보다 크면 변경되지 않는다")
    @Test
    void displayBiggerThanProductsAmount() {
        // given
        Menu menu = createMenu(탕수육_세트, "한그릇 세트", BigDecimal.valueOf(99999), 한그릇_세트_상품목록);
        when(menuRepository.findById(한그릇_세트.getId())).thenReturn(Optional.of(menu));

        // when
        // then
        assertThatThrownBy(() -> menuService.display(한그릇_세트.getId()))
                .isInstanceOf(IllegalStateException.class);
    }
}