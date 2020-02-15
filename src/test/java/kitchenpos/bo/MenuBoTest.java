package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MenuBoTest {

    @Mock private MenuDao menuDao;
    @Mock private MenuGroupDao menuGroupDao;
    @Mock private MenuProductDao menuProductDao;
    @Mock private ProductDao productDao;

    @InjectMocks
    private MenuBo menuBo;

    private static List<MenuProduct> expectedMenuProducts = new ArrayList<>();
    private static List<Menu> expectedMenus = new ArrayList<>();
    private static Menu expectedMenu;

    private static List<MenuProduct> actualMenuProducts = new ArrayList<>();
    private static List<Menu> actualMenus = new ArrayList<>();
    private static Menu actualMenu;

    private static List<Product> products = new ArrayList<>();

    @BeforeAll
    static void setup(){
        for(int i=1; i<=2; i++){
            Product product = new Product.Builder()
                .id((long)i)
                .name("치킨" + i)
                .price(new BigDecimal(10000 + (1000*i)))
                .build();
            products.add(product);
        }

        //expected DataSet
        for(int i=1 ;i<=2; i++){
            MenuProduct menuProduct = new MenuProduct.Builder()
                .seq((long) i)
                .menuId(1L)
                .productId(products.get(i-1).getId())
                .quantity(1)
                .build();

            expectedMenuProducts.add(menuProduct);
        }

        expectedMenu = new Menu.Builder()
            .id(1L)
            .name("뿌링클")
            .price(new BigDecimal(10000))
            .menuGroupId(1L)
            .menuProducts(expectedMenuProducts)
            .build();

        expectedMenus.add(expectedMenu);

        //Actual DataSet
        for(int i=1 ;i<=2; i++){
            MenuProduct menuProduct = new MenuProduct.Builder()
                .seq((long) i)
                .menuId(1L)
                .productId(products.get(i-1).getId())
                .quantity(1)
                .build();

            actualMenuProducts.add(menuProduct);
        }

        actualMenu = new Menu.Builder()
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
    void list(){
        when(menuDao.findAll()).thenReturn(actualMenus);
        when(menuProductDao.findAllByMenuId(actualMenu.getId())).thenReturn(actualMenuProducts);

        List<Menu> actual = menuBo.list();

        System.out.println(actual.hashCode());
        System.out.println(expectedMenus.hashCode());

        assertThat(actual).isEqualTo(expectedMenus);
    }

    @DisplayName("Menu 가격이 정해지지 않으면 IllegalArgumentException 이 발생한다.")
    @Test
    void createPriceIsNull (){
        Menu menu = new Menu.Builder()
            .id(1L)
            .name("뿌링클")
            .price(null)
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴가격이 0원 이하이면, IllegalArgumentException이 발생한다.")
    @Test
    void createPriceUnderZero (){
        Menu menu = new Menu.Builder()
            .id(1L)
            .name("뿌링클")
            .price(new BigDecimal(-1))
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴에 메뉴그룹 설정을 잘못한 경우, IllegalArgumentException이 발생한다.")
    @Test
    void createMenuWrongMenuGroup(){
        Menu menu = new Menu.Builder()
            .id(1L)
            .name("뿌링클")
            .price(new BigDecimal(10000))
            .menuGroupId(2L)
            .build();

        when(menuGroupDao.existsById(menu.getMenuGroupId())).thenReturn(false);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuBo.create(menu));
    }

    @DisplayName("메뉴를 구성하는 제품 가격의 총 합이 메뉴에 입력한 가격보다 크다.")
    @Test
    void createAllProductsPriceisGreaterThanPrice (){

        given(productDao.findById(1L)).willReturn(Optional.ofNullable(products.get(0)));
        given(productDao.findById(2L)).willReturn(Optional.ofNullable(products.get(1)));

        Menu menu = actualMenus.get(0);

        BigDecimal sum = BigDecimal.ZERO;
        for(final MenuProduct menuProduct : actualMenuProducts){
            final Product product = productDao.findById(menuProduct.getProductId()).get();
            sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));
        }

        assertThat(sum).isGreaterThan(menu.getPrice());
    }

    @DisplayName("메뉴에 입력한 가격이 메뉴를 구성하는 제품 가격의 총 합보다 크면 Exception이 발생한다.")
    @Test
    void createPriceisGreaterThanAllMenuProduct(){
        when(productDao.findById(1L)).thenReturn(Optional.ofNullable(products.get(0)));
        when(productDao.findById(2L)).thenReturn(Optional.ofNullable(products.get(1)));

        BigDecimal sum = BigDecimal.ZERO;
        for(final MenuProduct menuProduct : actualMenuProducts){
            final Product product = productDao.findById(menuProduct.getProductId()).get();
            sum = sum.add(product.getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())));
        }

        Menu highPriceMenu = new Menu.Builder()
            .price(sum.add(BigDecimal.valueOf(10000)))
            .build();

        assertThat(sum).isLessThan(highPriceMenu.getPrice());
    }
}
