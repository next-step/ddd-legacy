package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static kitchenpos.stub.MenuGroupStub.generateFirstTestMenuGroup;
import static kitchenpos.stub.MenuStub.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
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

    @DisplayName("새 메뉴를 등록할 수 있다.")
    @Test
    void createNewMenuGroup() {
        //given
        Menu newMenu = generateFiveThousandMenuProductsPriceVisibleSamePriceMenu();
        MenuGroup relatedMenuGroup = generateFirstTestMenuGroup();
        List<Product> relatedProducts = getProductsRelatedMenu(newMenu);
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(relatedMenuGroup));
        when(productRepository.findAllByIdIn(any())).thenReturn(relatedProducts);
        when(productRepository.findById(eq(newMenu.getMenuProducts().get(0).getProductId()))).thenReturn(Optional.of(relatedProducts.get(0)));
        when(productRepository.findById(eq(newMenu.getMenuProducts().get(1).getProductId()))).thenReturn(Optional.of(relatedProducts.get(1)));
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(menuRepository.save(any())).thenReturn(newMenu);

        //when
        Menu result = menuService.create(newMenu);

        //then
        assertThat(result).isEqualTo(newMenu);
    }

    @DisplayName("메뉴의 가격은 0 원 이상이어야 한다.")
    @Test
    void mustBePositivePrice() {
        //given
        Menu negativePriceMenu = generateNegativePriceMenu();

        //when & then
        assertThatThrownBy(() -> menuService.create(negativePriceMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 특정 메뉴그룹에 반드시 속해야 한다.")
    @Test
    void mustHaveBoundedMenuGroup() {
        //given
        Menu newMenu = generateFiveThousandMenuProductsPriceVisibleSamePriceMenu();
        when(menuGroupRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu)).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴는 1 개 이상의 상품을 포함하고 있어야 한다.")
    @Test
    void mustHaveOneOrMoreMenuProducts() {
        //given
        Menu emptyMenuProductsMenu = generateEmptyMenuProductsMenu();
        MenuGroup relatedMenuGroup = generateFirstTestMenuGroup();
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(relatedMenuGroup));

        //when & then
        assertThatThrownBy(() -> menuService.create(emptyMenuProductsMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴에 포함되는 상품은 미리 등록된 상품이어야 한다.")
    @Test
    void mustAlreadyCreatedProductsBoundedMenu() {
        //given
        Menu newMenu = generateFiveThousandMenuProductsPriceVisibleSamePriceMenu();
        MenuGroup relatedMenuGroup = generateFirstTestMenuGroup();
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(relatedMenuGroup));
        when(productRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴에 포함되는 상품의 수량은 0 개 이상이어야 한다.")
    @Test
    void mustBeOneOrMoreProductsBoundedMenu() {
        //given
        Menu negativeQuantityMenuProductMenu = generateContainingNegativeQuantityMenuProductMenu();
        MenuGroup relatedMenuGroup = generateFirstTestMenuGroup();
        List<Product> relatedProducts = getProductsRelatedMenu(negativeQuantityMenuProductMenu);
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(relatedMenuGroup));
        when(productRepository.findAllByIdIn(any())).thenReturn(relatedProducts);

        //when & then
        assertThatThrownBy(() -> menuService.create(negativeQuantityMenuProductMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴에 속한 메뉴상품들의 가격합보다 같거나 작아야한다.")
    @Test
    void mustBeMenuPriceSameOrSmallerThanMenuProductsSum() {
        //given
        Menu invalidPriceMenu = generateNineThousandMenuProductsPriceVisibleLargerPriceMenu();
        MenuGroup relatedMenuGroup = generateFirstTestMenuGroup();
        List<Product> relatedProducts = getProductsRelatedMenu(invalidPriceMenu);
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(relatedMenuGroup));
        when(productRepository.findAllByIdIn(any())).thenReturn(relatedProducts);
        when(productRepository.findById(eq(invalidPriceMenu.getMenuProducts().get(0).getProductId()))).thenReturn(Optional.of(relatedProducts.get(0)));
        when(productRepository.findById(eq(invalidPriceMenu.getMenuProducts().get(1).getProductId()))).thenReturn(Optional.of(relatedProducts.get(1)));

        //when & then
        assertThatThrownBy(() -> menuService.create(invalidPriceMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 이름은 비속어를 사용할 수 없다.")
    @Test
    void notAllowProfanity() {
        //given
        Menu newMenu = generateFiveThousandMenuProductsPriceVisibleSamePriceMenu();
        MenuGroup relatedMenuGroup = generateFirstTestMenuGroup();
        List<Product> relatedProducts = getProductsRelatedMenu(newMenu);
        when(menuGroupRepository.findById(any())).thenReturn(Optional.of(relatedMenuGroup));
        when(productRepository.findAllByIdIn(any())).thenReturn(relatedProducts);
        when(productRepository.findById(eq(newMenu.getMenuProducts().get(0).getProductId()))).thenReturn(Optional.of(relatedProducts.get(0)));
        when(productRepository.findById(eq(newMenu.getMenuProducts().get(1).getProductId()))).thenReturn(Optional.of(relatedProducts.get(1)));
        when(purgomalumClient.containsProfanity(any())).thenReturn(true);

        //when & then
        assertThatThrownBy(() -> menuService.create(newMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이미 등록 된 메뉴의 가격을 변경할 수 있다.")
    @Test
    void canChangePrice() {
        //given
        Menu createdMenu = generateFiveThousandMenuProductsPriceVisibleSamePriceMenu();
        Menu priceChangedMenu = generateFiveThousandMenuProductsPriceVisibleSmallerPriceMenu();
        when(menuRepository.findById(any())).thenReturn(Optional.of(createdMenu));

        //when
        Menu result = menuService.changePrice(createdMenu.getId(), priceChangedMenu);

        //then
        assertThat(result).isEqualTo(priceChangedMenu);
    }

    @DisplayName("이미 등록 된 메뉴를 노출할 수 있다.")
    @Test
    void canDisplayCreatedMenu() {
        //given
        Menu invisibleMenu = generateFiveThousandMenuProductsPriceInVisibleSamePriceMenu();
        when(menuRepository.findById(any())).thenReturn(Optional.of(invisibleMenu));

        //when
        Menu display = menuService.display(invisibleMenu.getId());

        //then
        assertThat(display.isDisplayed()).isTrue();
    }

    @DisplayName("이미 등록 된 메뉴를 숨길 수 있다.")
    @Test
    void canHideCreatedMenu() {
        //given
        Menu visibleMenu = generateFiveThousandMenuProductsPriceVisibleSamePriceMenu();
        when(menuRepository.findById(any())).thenReturn(Optional.of(visibleMenu));

        //when
        Menu hide = menuService.hide(visibleMenu.getId());

        //then
        assertThat(hide.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴 전체를 조회할 수 있다.")
    @Test
    void canFindAllMenus() {
        //given
        List<Menu> menus = new ArrayList<>();
        menus.add(generateFiveThousandMenuProductsPriceVisibleSamePriceMenu());
        menus.add(generateFiveThousandMenuProductsPriceVisibleSmallerPriceMenu());
        when(menuRepository.findAll()).thenReturn(menus);

        //when
        List<Menu> results = menuService.findAll();

        //then
        assertThat(results).containsExactlyElementsOf(menus);
    }

    private List<Product> getProductsRelatedMenu(Menu menu) {
        return menu.getMenuProducts()
                .stream()
                .map(MenuProduct::getProduct)
                .collect(Collectors.toList());
    }

}