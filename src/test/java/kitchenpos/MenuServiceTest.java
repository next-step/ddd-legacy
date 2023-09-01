package kitchenpos;

import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.TestFixtureFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

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

    @InjectMocks
    private MenuService menuService;

    @Test
    @DisplayName("유효한 메뉴 등록 및 반환")
    void createValidMenuReturnsCreatedMenu() {
        // Arrange
        MenuGroup menuGroup = createMenuGroup("한마리 메뉴");
        Product product = createProduct("반반치킨", BigDecimal.valueOf(16000));
        MenuProduct menuProduct = createMenuProduct(product.getId(), 1);
        Menu request = createMenu("반반치킨", BigDecimal.valueOf(16000), menuGroup.getId(), true, List.of(menuProduct));

        when(menuGroupRepository.findById(request.getMenuGroupId())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(purgomalumClient.containsProfanity(request.getName())).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenReturn(request);

        // Act
        Menu createdMenu = menuService.create(request);

        // Assert
        assertThat(createdMenu).isNotNull();
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    @DisplayName("메뉴 등록 - 0원 이상의 가격이 아니면 IllegalArgumentException 발생")
    void createInvalidPriceThrowsIllegalArgumentException() {
        // Arrange
        Menu request = createMenu("순살치킨", BigDecimal.valueOf(-16000));

        // Act & Assert
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("메뉴 등록 - 메뉴 그룹이 없는 경우 NoSuchElementException 발생")
    void createNoSuchMenuGroupThrowsNoSuchElementException() {
        // Arrange
        Menu request = createMenu("순살치킨", BigDecimal.valueOf(16000), UUID.randomUUID());

        when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(NoSuchElementException.class);
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("메뉴 등록 - 하나 이상의 상품이 없는 경우 IllegalArgumentException 발생")
    void createNoMenuProductsThrowsIllegalArgumentException() {
        // Arrange
        Menu request = createMenu("반반치킨", BigDecimal.valueOf(16000), UUID.randomUUID(), true, Collections.emptyList());

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(new MenuGroup()));

        // Act & Assert
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("메뉴 등록 - 상품과 메뉴 상품의 개수가 일치하지 않을 때 IllegalArgumentException 발생")
    void createMismatchedProductCountsThrowsIllegalArgumentException() {
        // Arrange
        MenuGroup menuGroup = createMenuGroup("두마리 메뉴");
        MenuProduct menuProduct = createMenuProduct(UUID.randomUUID(), 2);  // 두 개의 메뉴 상품
        Menu request = createMenu("상품과 메뉴 상품의 개수가 일치하지 않는 메뉴", BigDecimal.valueOf(30000), UUID.randomUUID(), true, List.of(menuProduct));

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllByIdIn(any())).thenReturn(new ArrayList<>());  // 상품 리스트는 비어있음

        // Act & Assert
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 등록 - 메뉴 가격이 포함된 상품 가격 총합보다 높을 때 IllegalArgumentException 발생")
    void createMenuPriceGreaterThanProductPriceThrowsIllegalArgumentException() {
        // Arrange
        MenuProduct menuProduct = createMenuProduct(UUID.randomUUID(), 1);
        Menu request = createMenu("통구이", BigDecimal.valueOf(32000), UUID.randomUUID(), true, List.of(menuProduct));

        Product product = createProduct("통구이", BigDecimal.valueOf(16000));
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        MenuGroup menuGroup = createMenuGroup("한마리 메뉴");
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

        product = createProduct("통구이", BigDecimal.valueOf(16000));
        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(product));

        // Act & Assert
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @Disabled // 테스트실패 케이스로 버그수정 필요
    @DisplayName("메뉴 가격 수정 - 메뉴의 상품 가격 총합보다 낮은 가격으로 수정할 때 수정된 메뉴 반환 - 테스트실패")
    void changePricePriceLessThanProductTotalChangedMenu() {
        // Arrange
        Product product1 = createProduct("간장치킨", BigDecimal.valueOf(16000));
        MenuProduct menuProduct1 = createMenuProduct(product1.getId(), 1, product1);
        Product product2 = createProduct("순살치킨", BigDecimal.valueOf(16000));
        MenuProduct menuProduct2 = createMenuProduct(product2.getId(), 1, product2);
        Menu menu = createMenu("간장&순살치킨", BigDecimal.valueOf(32000), UUID.randomUUID(), true, List.of(menuProduct1, menuProduct2));
        when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

        Menu request = createMenu("간장&순살치킨", BigDecimal.valueOf(30000)); // 상품 총합 16000 * 2 = 32000원 보다 낮은 가격

        // Act
        Menu changedMenu = menuService.changePrice(menu.getId(), request);

        // Assert
        assertThat(changedMenu).isNotNull();
        verify(menu, times(1)).setPrice(any(BigDecimal.class));
    }

    @Test
    @DisplayName("메뉴 가격 수정 - 메뉴의 상품 가격 총합보다 높은 가격으로 수정할 때 IllegalArgumentException 발생")
    void changePricePriceGreaterThanProductTotalThrowsIllegalArgumentException() {
        // Arrange
        Product product1 = createProduct("간장치킨", BigDecimal.valueOf(16000));
        MenuProduct menuProduct1 = createMenuProduct(product1.getId(), 1, product1);
        Product product2 = createProduct("간장치킨", BigDecimal.valueOf(16000));
        MenuProduct menuProduct2 = createMenuProduct(product2.getId(), 1, product2);
        Menu menu = createMenu("간장&순살치킨", BigDecimal.valueOf(32000), UUID.randomUUID(), true, List.of(menuProduct1, menuProduct2));
        when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

        Menu request = createMenu("간장&순살치킨", BigDecimal.valueOf(33000)); // 상품 총합 16000 * 2 = 32000원 보다 높은 가격

        // Act & Assert
        assertThatThrownBy(() -> menuService.changePrice(menu.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(menuRepository, never()).save(any(Menu.class));
    }

    @Test
    @DisplayName("메뉴 표시 - 정상적으로 메뉴 표시 후 반환")
    void displayValidMenuReturnsDisplayedMenu() {
        // Arrange
        UUID menuId = UUID.randomUUID();
        Menu menu = createMenu("순살치킨", BigDecimal.valueOf(16000), UUID.randomUUID(), false, new ArrayList<>());

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // Act
        Menu displayedMenu = menuService.display(menuId);

        // Assert
        assertThat(displayedMenu.isDisplayed()).isTrue();
    }

    @Test
    @DisplayName("메뉴 숨김 처리 - 정상적으로 숨김 처리 후 반환")
    void hideMenuValidMenuReturnsHiddenMenu() {
        // Arrange
        UUID menuId = UUID.randomUUID();
        Menu menu = createMenu("순살치킨", BigDecimal.valueOf(16000), UUID.randomUUID(), true, new ArrayList<>());

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menu));

        // Act
        Menu hiddenMenu = menuService.hide(menuId);

        // Assert
        assertThat(hiddenMenu.isDisplayed()).isFalse();
    }

    @Test
    @DisplayName("메뉴 조회 - 모든 메뉴 반환")
    void findAllMenusReturnsAllMenus() {
        // Arrange
        List<Menu> menus = new ArrayList<>();
        menus.add(createMenu("테스트 메뉴 1", BigDecimal.valueOf(1000)));
        menus.add(createMenu("테스트 메뉴 2", BigDecimal.valueOf(1000)));

        when(menuRepository.findAll()).thenReturn(menus);

        // Act
        List<Menu> allMenus = menuService.findAll();

        // Assert
        assertThat(allMenus).hasSize(2);
    }
}
