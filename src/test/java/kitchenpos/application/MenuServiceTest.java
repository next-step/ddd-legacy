package kitchenpos.application;

import kitchenpos.FixtureData;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest extends FixtureData {

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

    @BeforeEach
    void setUp() {
        fixtureMenus();
    }

    @DisplayName("메뉴 생성")
    @Test
    void createMenu() {
        // given
        Menu menu = menus.get(0);

        given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menu.getMenuGroup()));
        given(productRepository.findAllById(any())).willReturn(Arrays.asList(menu.getMenuProducts().get(0).getProduct()));
        given(productRepository.findById(any())).willReturn(Optional.of(products.get(0)));
        given(menuRepository.save(any())).willReturn(menu);

        // when
        Menu createMenu = menuService.create(menu);

        // then
        assertThat(createMenu).isNotNull();
    }

    @DisplayName("메뉴는 메뉴그룹에 포함안되면 예외처리")
    @Test
    void negativeEmptyMenuGroup() {
        Menu menu = menus.get(0);
        menu.setMenuGroupId(null);

        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴에 상품이 없으면 예외처리")
    @Test
    void negativeEmptyMenuProduct() {
        Menu menu = menus.get(0);

        given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menu.getMenuGroup()));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴에 상품 수량은 0 이하 예외 처리")
    @Test
    void negativeMenuProductQuantity() {
        // given
        Menu menu = menus.get(0);
        MenuProduct menuProduct = menu.getMenuProducts().get(0);
        menuProduct.setQuantity(0);

        given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menu.getMenuGroup()));
        given(productRepository.findAllById(any())).willReturn(Arrays.asList(menu.getMenuProducts().get(0).getProduct()));
        given(productRepository.findById(any())).willReturn(Optional.of(products.get(0)));

        // when, then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 가격이 메뉴의 상품 가격이 보다 높으면 예외처리")
    @Test
    void negativeMenuProductPrice() {
        // given
        Menu menu = menus.get(0);
        Product product = menu.getMenuProducts().get(0).getProduct();
        menu.setPrice(ofPrice(1).add(product.getPrice()));

        given(menuGroupRepository.findById(menu.getMenuGroupId())).willReturn(Optional.of(menu.getMenuGroup()));
        given(productRepository.findAllById(any())).willReturn(Arrays.asList(product));
        given(productRepository.findById(any())).willReturn(Optional.of(products.get(0)));

        // when, then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 가격은 변경 가능")
    @Test
    void changeMenuPrice() {
        // given
        Menu menu = menus.get(0);
        menu.setPrice(ofPrice(100));

        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        // when
        Menu changeMenu = menuService.changePrice(menu.getId(), menu);

        assertThat(changeMenu.getPrice()).isEqualTo(menu.getPrice());
    }

    @DisplayName("메뉴 가격 변경 시 조회안되면 예외")
    @Test
    void negativeMenuFind() {
        Menu menu = menus.get(0);

        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴 가격 없으면 예외 처리")
    @Test
    void negativeChangeMenuPrice() {
        Menu menu = menus.get(0);
        menu.setPrice(null);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
    }

    @DisplayName("메뉴 가격 변경 시 상품들 가격보다 높으면 예외 처리")
    @Test
    void menuPriceHigherThenProductPrice() {
        Menu menu = menus.get(0);
        menu.setPrice(ofPrice(10000000));

        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> menuService.changePrice(menu.getId(), menu));
    }

    @DisplayName("메뉴 노출")
    @Test
    void menuDisplay() {
        // given
        Menu menu = menus.get(0);

        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        // when
        Menu displayMenu = menuService.display(menu.getId());

        // then
        assertThat(displayMenu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴 숨김")
    @Test
    void menuHide() {
        Menu menu = menus.get(0);

        given(menuRepository.findById(menu.getId())).willReturn(Optional.of(menu));

        Menu hideMenu = menuService.hide(menu.getId());

        assertThat(hideMenu.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴 조회")
    @Test
    void findAll() {
        // given
        given(menuRepository.findAll()).willReturn(menus);

        // when
        List<Menu> findAll = menuService.findAll();

        // then
        verify(menuRepository).findAll();
        verify(menuRepository, times(1)).findAll();
        assertAll(
                () -> assertThat(menus.containsAll(findAll)).isTrue(),
                () -> assertThat(menus.size()).isEqualTo(findAll.size())
        );
    }
}