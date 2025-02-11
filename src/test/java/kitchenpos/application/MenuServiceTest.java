package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final MenuGroupRepository menuGroupRepository = mock(MenuGroupRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    private final MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void create() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        when(menuGroupRepository.findById(any(UUID.class))).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));
        when(purgomalumClient.containsProfanity(any(String.class))).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenReturn(menu);

        // when
        Menu resultMenu = menuService.create(menu);

        // then
        assertThat(resultMenu.getId()).isNotNull();
        assertThat(resultMenu.getName()).isEqualTo(menu.getName());
        assertThat(resultMenu.getPrice()).isEqualTo(menu.getPrice());
        assertThat(resultMenu.getMenuGroup()).isEqualTo(menu.getMenuGroup());
        assertThat(resultMenu.getMenuProducts()).isEqualTo(menu.getMenuProducts());
        assertThat(resultMenu.isDisplayed()).isEqualTo(menu.isDisplayed());
    }

    @DisplayName("메뉴 가격은 0원보다 작다면 에러를 발생시킨다.")
    @Test
    void minusPrice() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(-1), menuGroup, menuProduct);

        // then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 모음이 하나 이상의 메뉴로 구성되어있지 않다면 에러를 발생시킨다.")
    @Test
    void noMenuInMenuGroup() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        // then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴가 하나 이상의 상품으로 구성되어있지 않다면 에러를 발생시킨다.")
    @Test
    void noProductInMenu() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        // when
        when(menuGroupRepository.findById(any(UUID.class))).thenReturn(Optional.of(menuGroup));

        // then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴에 등록된 모든 상품이 등록되어있지 않다면 에러를 발생시킨다.")
    @Test
    void noMenuProduct() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        // when
        when(menuGroupRepository.findById(any(UUID.class))).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

        // then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴명에 비속어가 들어있다면 에러를 발생시킨다.")
    @Test
    void hasProfanity() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("fuck", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        // when
        when(menuGroupRepository.findById(any(UUID.class))).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(product));

        // then
        assertThatThrownBy(() -> menuService.create(menu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 변경이 가능하다.")
    @Test
    void changePrice() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);
        Menu changeMenu = makeTestMenu("중식", BigDecimal.valueOf(4000), menuGroup, menuProduct);

        when(menuRepository.findById(any(UUID.class))).thenReturn(Optional.of(menu));

        // when
        Menu resultMenu = menuService.changePrice(menu.getId(), changeMenu);

        // then
        assertThat(resultMenu.getPrice()).isEqualTo(changeMenu.getPrice());
    }

    @DisplayName("메뉴 상품들의 가격의 총합이 메뉴의 전체 가격보다 낮으면 에러를 발생시킨다.")
    @Test
    void compareMenuPrice() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);
        Menu changeMenu = makeTestMenu("중식", BigDecimal.valueOf(6000), menuGroup, menuProduct);

        when(menuRepository.findById(any(UUID.class))).thenReturn(Optional.of(menu));

        // when
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), changeMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 노출할 수 있다.")
    @Test
    void display() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        when(menuRepository.findById(any(UUID.class))).thenReturn(Optional.of(menu));

        // when
        Menu resultMenu = menuService.display(menu.getId());

        // then
        assertThat(resultMenu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 노출하지 않을 수 있다.")
    @Test
    void hide() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        when(menuRepository.findById(any(UUID.class))).thenReturn(Optional.of(menu));

        // when
        Menu resultMenu = menuService.hide(menu.getId());

        // then
        assertThat(resultMenu.isDisplayed()).isFalse();
    }
}
