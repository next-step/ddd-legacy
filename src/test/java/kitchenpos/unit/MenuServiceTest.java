package kitchenpos.unit;

import kitchenpos.application.MenuService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.domain.ProfanityChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.unit.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    MenuRepository menuRepository;

    @Mock
    MenuGroupRepository menuGroupRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    ProfanityChecker profanityChecker;

    @InjectMocks
    MenuService menuService;


    @DisplayName("메뉴의 가격은 0원보다 작을 수 없다.")
    @Test
    void create_Illegal_NegativePrice() {
        // given
        Menu request = aManWonChickenMenu(-10_000);

        // when + then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 존재하지 않는 메뉴 그룹에 포함될 수 없다.")
    @Test
    void create_Illegal_MenuGroup() {
        // given
        Menu request = aManWonChickenMenu(10_000);
        request.setMenuGroupId(UUID.randomUUID());

        // when + then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴는 상품들을 포함해야한다.")
    @Test
    void create_Illegal_EmptyMenuProducts() {
        // given
        MenuGroup menuGroup = aMenuGroup();
        Menu request = aManWonChickenMenu(10_000);
        request.setMenuGroupId(menuGroup.getId());
        request.setMenuProducts(Collections.emptyList());

        when(menuGroupRepository.findById(menuGroup.getId())).thenReturn(Optional.of(menuGroup));

        // when + then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴에 포함된 상품들은 존재하는 상품이어야 한다.")
    @Test
    void create_Illegal_NotExistingProducts() {
        // given
        Menu request = aManWonChickenMenu(10_000);

        when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(request.getMenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

        // when + then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴상품 가격의 총합보다 클 수 없다.")
    @Test
    void create_Illegal_TooMuchPrice() {
        // given
        MenuProduct menuProduct = aChickenMenuProduct(10_000, 1);
        Product product = menuProduct.getProduct();
        Menu request = aMenu("후라이드 치킨", 30_000, menuProduct);

        when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(request.getMenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(product));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        // when + then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름은 비속어를 포함할 수 없다.")
    @Test
    void create_Illegal_ProfaneName() {
        // given
        MenuProduct menuProduct = aChickenMenuProduct(10_000, 1);
        Product product = menuProduct.getProduct();
        Menu request = aMenu("바보", 10_000, menuProduct);

        when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(request.getMenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(product));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(profanityChecker.containsProfanity("바보")).thenReturn(true);

        // when + then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 생성")
    @Test
    void create() {
        // given
        MenuProduct menuProduct = aChickenMenuProduct(10_000, 1);
        Product product = menuProduct.getProduct();
        Menu request = aMenu("후라이드 치킨", 10_000, menuProduct);

        when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(request.getMenuGroup()));
        when(productRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(product));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(menuRepository.save(any())).then(i -> i.getArgument(0, Menu.class));

        // when
        Menu saved = menuService.create(request);

        // then
        assertThat(saved.getId()).isNotNull();
    }

    @DisplayName("변경하려는 메뉴의 가격은 0원 이상이어야 한다.")
    @Test
    void changePrice_Illegal_NegativePrice() {
        // given
        Menu menu = aManWonChickenMenu(10_000);
        Menu changeRequest = new Menu();
        changeRequest.setPrice(BigDecimal.valueOf(-10_000));

        // when + then
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), changeRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("변경하려는 메뉴의 가격은 메뉴 상품들의 총합보다 클 수 없다.")
    @Test
    void changePrice_Illegal_TooMuchPrice() {
        // given
        Menu menu = aManWonChickenMenu(10_000);
        Menu changeRequest = new Menu();
        changeRequest.setPrice(BigDecimal.valueOf(30_000));

        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when + then
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), changeRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격 변경")
    @Test
    void changePrice() {
        // given
        Menu menu = aManWonChickenMenu(10_000);
        Menu changeRequest = new Menu();
        changeRequest.setPrice(BigDecimal.valueOf(9_000));

        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when
        Menu changed = menuService.changePrice(menu.getId(), changeRequest);

        // then
        assertThat(changed.getPrice()).isEqualTo(BigDecimal.valueOf(9_000));
    }

    @DisplayName("가격 정책에 맞지 않는 메뉴는 진열할 수 없다.")
    @Test
    void display_Illegal_TooMuchPrice() {
        // given
        Menu menu = aManWonChickenMenu(20_000);

        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when
        assertThatThrownBy(() -> menuService.display(menu.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("가격 정책에 맞는 메뉴는 진열할 수 있다.")
    @Test
    void display() {
        // given
        Menu menu = aManWonChickenMenu(10_000);

        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when
        Menu displayedMenu = menuService.display(menu.getId());

        // then
        assertThat(displayedMenu.isDisplayed()).isTrue();
    }

}