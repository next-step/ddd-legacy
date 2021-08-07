package kitchenpos.unit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.jsonpath.TypeRef;
import kitchenpos.application.MenuService;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MenuTest extends UnitTestRunner {

    @InjectMocks
    private MenuService menuService;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurgomalumClient purgomalumClient;

    @DisplayName("메뉴를 생성한다.")
    @Test
    public void create() {
        //given
        final MenuGroup stubbedMenuGroup = new MenuGroup();
        final String menuGroupName = "메인 메뉴";
        final UUID menuGroupId = UUID.randomUUID();
        stubbedMenuGroup.setId(menuGroupId);
        stubbedMenuGroup.setName(menuGroupName);

        when(menuGroupRepository.findById(menuGroupId)).thenReturn(Optional.of(stubbedMenuGroup));

        final UUID productId_1 = UUID.randomUUID();
        final BigDecimal productPrice_1 = BigDecimal.valueOf(10000);
        final Product stubbedProduct = new Product();
        stubbedProduct.setId(productId_1);
        stubbedProduct.setPrice(productPrice_1);

        when(productRepository.findAllById(List.of(productId_1))).thenReturn(List.of(stubbedProduct));
        when(productRepository.findById(productId_1)).thenReturn(Optional.of(stubbedProduct));

        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId_1);
        menuProduct.setQuantity(1L);

        final BigDecimal menuPrice = BigDecimal.valueOf(10000);
        final Menu request = new Menu();
        final String menuName = "후라이드 치킨";
        request.setPrice(menuPrice);
        request.setMenuGroupId(menuGroupId);
        request.setMenuProducts(List.of(menuProduct));
        request.setName(menuName);
        request.setDisplayed(true);

        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setId(menuId);
        stubbedMenu.setName(menuName);
        stubbedMenu.setPrice(menuPrice);
        stubbedMenu.setDisplayed(true);
        stubbedMenu.setMenuProducts(List.of(menuProduct));

        when(menuRepository.save(any(Menu.class))).thenReturn(stubbedMenu);

        //when
        final Menu createMenu = menuService.create(request);

        //then
        assertThat(createMenu.getId()).isEqualTo(menuId);
        assertThat(createMenu.getName()).isEqualTo(menuName);
        assertThat(createMenu.getPrice()).isEqualTo(menuPrice);
        assertThat(createMenu.isDisplayed()).isEqualTo(true);
        assertThat(createMenu.getMenuProducts().get(0)).isEqualTo(menuProduct);
        verify(purgomalumClient, times(1)).containsProfanity(menuName);
    }

    @DisplayName("해당 메뉴의 가격을 변경한다.")
    @Test
    public void changePrice() {
        //given
        final BigDecimal changePrice = BigDecimal.valueOf(15000);
        final Menu request = new Menu();
        request.setPrice(changePrice);

        final Product stubbedProduct = new Product();
        stubbedProduct.setId(UUID.randomUUID());
        stubbedProduct.setPrice(BigDecimal.valueOf(20000));

        final MenuProduct stubbedMenuProduct = new MenuProduct();
        stubbedMenuProduct.setQuantity(1);
        stubbedMenuProduct.setProduct(stubbedProduct);

        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");
        stubbedMenu.setPrice(BigDecimal.valueOf(20000));
        stubbedMenu.setDisplayed(true);
        stubbedMenu.setMenuProducts(List.of(stubbedMenuProduct));

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(stubbedMenu));

        //when
        final Menu changePriceMenu = menuService.changePrice(menuId, request);

        //then
        assertThat(changePriceMenu.getPrice()).isEqualTo(changePrice);
    }

    @DisplayName("해당 메뉴를 노출 시킨다.")
    @Test
    public void display() {
        //given
        final BigDecimal changePrice = BigDecimal.valueOf(15000);
        final Menu request = new Menu();
        request.setPrice(changePrice);

        final Product stubbedProduct = new Product();
        stubbedProduct.setId(UUID.randomUUID());
        stubbedProduct.setPrice(BigDecimal.valueOf(20000));

        final MenuProduct stubbedMenuProduct = new MenuProduct();
        stubbedMenuProduct.setQuantity(1);
        stubbedMenuProduct.setProduct(stubbedProduct);

        final Menu stubbedMenu = new Menu();
        final UUID menuId = UUID.randomUUID();
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");
        stubbedMenu.setPrice(BigDecimal.valueOf(20000));
        stubbedMenu.setDisplayed(false);
        stubbedMenu.setMenuProducts(List.of(stubbedMenuProduct));

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(stubbedMenu));

        //when
        final Menu displayMenu = menuService.display(menuId);

        //then
        assertThat(displayMenu.isDisplayed()).isTrue();
    }

    @DisplayName("해당 메뉴를 감춘다.")
    @Test
    public void hide() {
        //given
        final UUID menuId = UUID.randomUUID();
        final Menu stubbedMenu = new Menu();
        stubbedMenu.setId(menuId);
        stubbedMenu.setName("후라이드 치킨");
        stubbedMenu.setPrice(BigDecimal.valueOf(20000));
        stubbedMenu.setDisplayed(true);

        when(menuRepository.findById(menuId)).thenReturn(Optional.of(stubbedMenu));

        //when
        final Menu hideMenu = menuService.hide(menuId);

        //then
        assertThat(hideMenu.isDisplayed()).isFalse();
    }

    @DisplayName("모든 메뉴를 조회한다.")
    @Test
    public void findAll() {
        //given
        final UUID menuId = UUID.randomUUID();
        final Menu stubbedMenu_1 = new Menu();
        stubbedMenu_1.setId(menuId);
        stubbedMenu_1.setName("후라이드 치킨");
        stubbedMenu_1.setPrice(BigDecimal.valueOf(20000));
        stubbedMenu_1.setDisplayed(true);

        final Menu stubbedMenu_2 = new Menu();
        stubbedMenu_2.setId(menuId);
        stubbedMenu_2.setName("양념 치킨");
        stubbedMenu_2.setPrice(BigDecimal.valueOf(21000));
        stubbedMenu_2.setDisplayed(true);

        final Menu stubbedMenu_3 = new Menu();
        stubbedMenu_3.setId(menuId);
        stubbedMenu_3.setName("간장 치킨");
        stubbedMenu_3.setPrice(BigDecimal.valueOf(21000));
        stubbedMenu_3.setDisplayed(true);

        when(menuRepository.findAll()).thenReturn(List.of(stubbedMenu_1, stubbedMenu_2, stubbedMenu_3));

        //when
        final List<Menu> menus = menuService.findAll();

        //then
        assertThat(menus.size()).isEqualTo(3);
        assertThat(menus.get(0)).isEqualTo(stubbedMenu_1);
        assertThat(menus.get(1)).isEqualTo(stubbedMenu_2);
        assertThat(menus.get(2)).isEqualTo(stubbedMenu_3);

    }

}
