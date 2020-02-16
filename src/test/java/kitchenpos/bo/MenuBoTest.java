package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuGroup;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MockitoSettings(strictness = Strictness.LENIENT)
class MenuBoTest {

    @Mock
    private MenuDao menuDao;

    @Mock
    private MenuGroupDao menuGroupDao;

    @Mock
    private MenuProductDao menuProductDao;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private MenuBo menuBo;

    // Fixture
    private MenuGroup menuGroup;
    private Product product;
    private MenuProduct menuProduct1;
    private MenuProduct menuProduct2;
    private Menu menu1;
    private Menu menu2;

    @BeforeEach
    void setUp() {
        prepareFixture();
        prepareMockito();
    }

    void prepareFixture() {
        menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("메뉴 그룹");

        product = new Product();
        product.setId(1L);
        product.setName("양념치킨");
        product.setPrice(BigDecimal.valueOf(13000));

        menuProduct1 = new MenuProduct();
        menuProduct1.setMenuId(1L);
        menuProduct1.setProductId(1L);
        menuProduct1.setQuantity(2);
        menuProduct1.setSeq(1L);

        menuProduct2 = new MenuProduct();
        menuProduct2.setMenuId(2L);
        menuProduct2.setProductId(1L);
        menuProduct2.setQuantity(2);
        menuProduct2.setSeq(1L);

        menu1 = new Menu();
        menu1.setId(1L);
        menu1.setMenuGroupId(1L);
        menu1.setName("1+1치킨");
        menu1.setMenuProducts(Arrays.asList(menuProduct1));
        menu1.setPrice(BigDecimal.valueOf(20000));

        menu2 = new Menu();
        menu2.setId(2L);
        menu2.setMenuGroupId(1L);
        menu2.setName("갈릭세트");
        menu2.setMenuProducts(Arrays.asList(menuProduct2));
        menu2.setPrice(BigDecimal.valueOf(20000));
    }

    void prepareMockito() {
        Mockito.when(menuGroupDao.existsById(menu1.getMenuGroupId())).thenReturn(true);
        Mockito.when(menuGroupDao.existsById(menu2.getMenuGroupId())).thenReturn(true);
        Mockito.when(productDao.findById(product.getId())).thenReturn(Optional.ofNullable(product));
        Mockito.when(menuDao.save(menu1)).thenReturn(menu1);
        Mockito.when(menuDao.save(menu2)).thenReturn(menu2);
        Mockito.when(menuProductDao.save(menuProduct1)).thenReturn(menuProduct1);
        Mockito.when(menuProductDao.save(menuProduct2)).thenReturn(menuProduct2);
    }

    @DisplayName("사용자는 메뉴를 등록할 수 있고, 등록이 완료되면 등록된 메뉴 정보를 반환받아 확인할 수 있다")
    @Test
    void create() {
        //given
        //when
        Menu actual = menuBo.create(menu1);

        //then
        assertThat(actual.getId()).isEqualTo(menu1.getId());
        assertThat(actual.getMenuGroupId()).isEqualTo(menu1.getMenuGroupId());
        assertThat(actual.getName()).isEqualTo(menu1.getName());
        assertThat(actual.getPrice()).isEqualTo(menu1.getPrice());
        assertThat(actual.getMenuProducts()).containsExactlyInAnyOrder(menuProduct1);
    }


    @DisplayName("등록하려는 메뉴는 가격이 있어야 하고, 가격은 0원 이상이어야 한다")
    @Test
    void create_menu_with_price() {
        //given
        menu1.setPrice(null);
        menu2.setPrice(BigDecimal.valueOf(-1000));

        //when & then
        assertThatThrownBy(() -> {
            menuBo.create(menu1);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            menuBo.create(menu2);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴는 1개의 메뉴 그룹에 포함되어야 한다")
    @Test
    void create_menu_with_menu_group() {
        //given
        Mockito.when(menuGroupDao.existsById(menu1.getMenuGroupId())).thenReturn(false);

        //when
        //then
        assertThatThrownBy(() -> {
            menuBo.create(menu1);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 음식을 '메뉴 구성 음식'에 포함시킬 수 없다")
    @Test
    void create_menu_with_menu_products() {
        //given
        Mockito.when(productDao.findById(product.getId())).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> {
            menuBo.create(menu1);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 메뉴를 구성하는 '메뉴 구성 음식'들의 가격 총합을 넘어서는 안된다")
    @Test
    void create_menu_with_sum_of_menu_products() {
        //given
        menu1.setPrice(BigDecimal.valueOf(100000));

        //when
        //then
        assertThatThrownBy(() -> {
            menuBo.create(menu1);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록된 메뉴에 포함된 메뉴 구성 음식 정보는 각각 메뉴 번호, 음식 번호, 음식 수량을 포함한다")
    @Test
    void create_menu_with_menu_product() {
        //given
        //when
        Menu menu = menuBo.create(menu1);
        List<MenuProduct> actual = menu.getMenuProducts();

        //then
        for(MenuProduct menuProduct: actual) {
            assertThat(menuProduct.getMenuId()).isNotNull();
            assertThat(menuProduct.getProductId()).isNotNull();
            assertThat(menuProduct.getQuantity()).isNotNull();
            assertThat(menuProduct.getSeq()).isNotNull();
        }
    }

    @DisplayName("사용자는 등록된 모든 메뉴의 목록을 조회할 수 있다")
    @Test
    void list() {
        //given
        Mockito.when(menuDao.findAll()).thenReturn(Arrays.asList(menu1, menu2));

        //when
        List<Menu> actual = menuBo.list();

        //then
        assertThat(actual).containsExactlyInAnyOrder(menu1, menu2);
    }
}
