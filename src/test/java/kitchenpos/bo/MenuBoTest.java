package kitchenpos.bo;

import kitchenpos.dao.DefaultProductDao;
import kitchenpos.dao.DefaultMenuDao;
import kitchenpos.dao.DefaultMenuGroupDao;
import kitchenpos.dao.DefaultMenuProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import kitchenpos.support.MenuBuilder;
import kitchenpos.support.MenuProductBuilder;
import kitchenpos.support.ProductBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MenuBoTest {

    private static List<MenuProduct> expectedMenuProducts = new ArrayList<>();
    private static List<Menu> expectedMenus = new ArrayList<>();
    private static Menu expectedMenu;
    private static List<MenuProduct> actualMenuProducts = new ArrayList<>();
    private static List<Menu> actualMenus = new ArrayList<>();
    private static Menu actualMenu;
    private static List<Product> products = new ArrayList<>();
    private static BigDecimal totalPrice = BigDecimal.ZERO;
    @Mock
    private DefaultMenuDao menuDao;
    @Mock
    private DefaultMenuGroupDao menuGroupDao;
    @Mock
    private DefaultMenuProductDao menuProductDao;
    @Mock
    private DefaultProductDao productDao;
    @InjectMocks
    private MenuBo menuBo;

    @BeforeAll
    static void setup() {
        for (int i = 1; i <= 2; i++) {
            Product product = new ProductBuilder()
                .id((long) i)
                .name("치킨" + i)
                .price(new BigDecimal(10000 + (1000 * i)))
                .build();
            products.add(product);
        }

        //expected DataSet
        for (int i = 1; i <= 2; i++) {
            MenuProduct menuProduct = new MenuProductBuilder()
                .seq((long) i)
                .menuId(1L)
                .productId(products.get(i - 1).getId())
                .quantity(1)
                .build();

            totalPrice.add(products.get(i - 1).getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));

            expectedMenuProducts.add(menuProduct);
        }

        expectedMenu = new MenuBuilder()
            .id(1L)
            .name("뿌링클")
            .price(new BigDecimal(10000))
            .menuGroupId(1L)
            .menuProducts(expectedMenuProducts)
            .build();

        expectedMenus.add(expectedMenu);

        //Actual DataSet
        for (int i = 1; i <= 2; i++) {
            MenuProduct menuProduct = new MenuProductBuilder()
                .seq((long) i)
                .menuId(1L)
                .productId(products.get(i - 1).getId())
                .quantity(1)
                .build();

            actualMenuProducts.add(menuProduct);
        }

        actualMenu = new MenuBuilder()
            .id(1L)
            .name("뿌링클")
            .price(new BigDecimal(10000))
            .menuGroupId(1L)
            .menuProducts(actualMenuProducts)
            .build();

        actualMenus.add(actualMenu);
    }

    @DisplayName("메뉴 리스트를 불러온다.")
    @Test
    void list() {
        given(menuDao.findAll()).willReturn(actualMenus);
        given(menuProductDao.findAllByMenuId(actualMenu.getId())).willReturn(actualMenuProducts);

        List<Menu> actual = menuBo.list();

        assertThat(actual).isEqualTo(expectedMenus);
    }

    @DisplayName("Menu 가격이 정해지지 않으면 IllegalArgumentException 이 발생한다.")
    @Test
    void createPriceIsNull() {
        Menu menu = new MenuBuilder()
            .id(1L)
            .name("뿌링클")
            .price(null)
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴가격이 0원 이하이면, IllegalArgumentException이 발생한다.")
    @Test
    void createPriceUnderZero() {
        Menu menu = new MenuBuilder()
            .id(1L)
            .name("뿌링클")
            .price(new BigDecimal(-1))
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴에 메뉴그룹 설정을 잘못한 경우, IllegalArgumentException이 발생한다.")
    @Test
    void createMenuWrongMenuGroup() {
        Menu menu = new MenuBuilder()
            .id(1L)
            .name("뿌링클")
            .price(new BigDecimal(10000))
            .menuGroupId(2L)
            .build();

        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(false);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴에 입력한 가격이 메뉴를 구성하는 제품 가격의 총 합보다 크면 Exception이 발생한다.")
    @Test
    void createPriceisGreaterThanAllMenuProduct() {
        Menu menu = new MenuBuilder()
            .id(expectedMenu.getId())
            .name(expectedMenu.getName())
            .price(totalPrice.add(BigDecimal.valueOf(100000)))
            .menuGroupId(expectedMenu.getMenuGroupId())
            .menuProducts(expectedMenu.getMenuProducts())
            .build();

        given(menuGroupDao.existsById(menu.getMenuGroupId())).willReturn(true);
        given(productDao.findById(menu.getMenuProducts().get(0).getProductId()))
            .willReturn(Optional.ofNullable(products.get(0)));
        given(productDao.findById(menu.getMenuProducts().get(1).getProductId()))
            .willReturn(Optional.ofNullable(products.get(1)));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("입력한 값과 출력한 값이 동일하다.")
    @Test
    void create() {
        given(menuGroupDao.existsById(expectedMenu.getMenuGroupId())).willReturn(true);
        given(productDao.findById(expectedMenu.getMenuProducts().get(0).getProductId()))
            .willReturn(Optional.ofNullable(products.get(0)));
        given(productDao.findById(expectedMenu.getMenuProducts().get(1).getProductId()))
            .willReturn(Optional.ofNullable(products.get(1)));
        given(menuDao.save(expectedMenu)).willReturn(actualMenu);

        Menu savedMenu = menuBo.create(expectedMenu);

        assertThat(savedMenu).isEqualToComparingFieldByField(expectedMenu);
    }
}
