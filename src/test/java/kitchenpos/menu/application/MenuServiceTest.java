package kitchenpos.menu.application;

import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static kitchenpos.menu.step.MenuStep.*;
import static kitchenpos.product.step.ProductStep.createProduct;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DisplayName("Menu 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

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

    @DisplayName("메뉴 가격은 0 이상이다")
    @Test
    public void createWithValidPrice() {
        // given
        Menu menu = createMenu(-1);

        // when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 등록되어 있는 메뉴 그룹에 포함된다")
    @Test
    public void createWithinRegisteredMenuGroup() {
        // given
        Menu menu = createMenu(17000);
        when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴는 메뉴 상품을 가져야 한다")
    @Test
    public void createMenuProductToBeRegisteredAsProduct() {
        // given
        Menu menu = createMenu(17000, Collections.emptyList());
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "두마리메뉴");

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));

        // when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 상품은 모두 상품으로 등록되어 있어야 한다")
    @Test
    public void createWithValidNumberOfMenuProduct() {
        // given
        MenuProduct 후라이드 = createMenuProduct(createProduct("후라이드", 16000), 2L, null);
        Menu menu = createMenu(17000, Arrays.asList(후라이드));
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "두마리메뉴");

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllById(any())).thenReturn(Collections.emptyList());

        // when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 수량은 0 이상이다")
    @Test
    public void createWithValidQuantity() {
        // given
        MenuProduct 후라이드 = createMenuProduct(createProduct("후라이드", 16000), -1L, null);
        Menu menu = createMenu(17000, Arrays.asList(후라이드));
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "두마리메뉴");

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllById(any())).thenReturn(Collections.emptyList());

        // when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴 상품들의 (가격 * 수량) 합을 넘을 수 없다")
    @Test
    public void createWithLowerPriceThanSumOfMenuProduct() {
        // given
        Product 후라이드 = createProduct("후라이드", 16000);
        MenuProduct menuProduct = createMenuProduct(후라이드, 1L, null);
        Menu menu = createMenu(17000, Arrays.asList(menuProduct));
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "두마리메뉴");

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllById(any())).thenReturn(Arrays.asList(후라이드));
        when(productRepository.findById(any())).thenReturn(Optional.of(후라이드));

        // when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 이름은 욕설을 포함하지 않는다")
    @Test
    public void createWithoutBadWord() {
        // given
        Product 후라이드 = createProduct("후라이드", 16000);
        MenuProduct menuProduct = createMenuProduct(후라이드, 2L, null);
        Menu menu = createMenu("fuck",17000, Arrays.asList(menuProduct));
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "두마리메뉴");

        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(menuGroup));
        when(productRepository.findAllById(any())).thenReturn(Arrays.asList(후라이드));
        when(productRepository.findById(any())).thenReturn(Optional.of(후라이드));
        when(purgomalumClient.containsProfanity(anyString())).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
