package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuBoTests {

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

    private Menu mockMenu = new Menu();
    private MenuProduct mockMenuProduct = new MenuProduct();
    private Product mockProduct = new Product();
    private final List<Menu> mockMenus = new ArrayList<>();
    private final List<MenuProduct> mockMenuProducts = new ArrayList<>();

    @BeforeEach
    public void defaultSetup() {
        // Menu { id: 1L, menuGroupId: 1L, price: 16000, name: testMenu, menuProducts: mockMenuProducts }
        setupDefaultMenu();

        // MenuProduct { id: 1L, menuId: 1L, quantity: 100, productId: 2L }
        setupDefaultMenuProduct();

        // Product { id: 2L, name: testProduct, price: 16000 }
        setupDefaultProduct();

        mockMenus.add(mockMenu);
        mockMenuProducts.add(mockMenuProduct);
    }

    @DisplayName("메뉴 전체 조회 시 메뉴에 맞는 메뉴 상품들을 가져오는지 확인")
    @Test
    public void findAllTest() {
        given(menuDao.findAll()).willReturn(mockMenus);
        given(menuProductDao.findAllByMenuId(1L)).willReturn(mockMenuProducts);

        List<Menu> menuList = menuBo.list();

        assertThat(menuList.get(0).getId()).isEqualTo(1L);
        assertThat(menuList.get(0).getMenuProducts().get(0).getMenuId()).isEqualTo(1L);
    }

    @DisplayName("정상적인 값들로 메뉴 생성 시도 성공")
    @Test
    public void createMenuSuccess() {
        given(menuGroupDao.existsById(1L)).willReturn(true);
        given(productDao.findById(2L)).willReturn(Optional.ofNullable(mockProduct));
        given(menuDao.save(mockMenu)).willReturn(mockMenu);
        given(menuProductDao.save(mockMenuProduct)).willReturn(mockMenuProduct);

        Menu saved = menuBo.create(mockMenu);

        assertThat(saved.getMenuProducts().get(0).getMenuId()).isEqualTo(1L);
    }

    @DisplayName("0보다 작은 가격으로 메뉴 생성 시도 시 실패")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    public void createMenuFailWithInvalidPrice(int price) {
        mockMenu.setPrice(BigDecimal.valueOf(price));

        assertThatThrownBy(() -> menuBo.create(mockMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴에 속한 메뉴 상품 가격의 총합보다 높은 가격으로 메뉴 생성 시도 시 실패")
    @ParameterizedTest
    @ValueSource(ints = {1000000, 100000000, 777777})
    public void createMenuFailWhenMenuPriceIsBiggerThanPriceSum(int price) {
        mockMenu.setPrice(BigDecimal.valueOf(price));
        given(menuGroupDao.existsById(1L)).willReturn(true);
        given(productDao.findById(2L)).willReturn(Optional.ofNullable(mockProduct));

        assertThatThrownBy(() -> menuBo.create(mockMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹에 속하지 않은 메뉴로 생성 시도 시 실패")
    @Test
    public void createMenuFailWithNotInMenuGroup() {
        given(menuGroupDao.existsById(1L)).willReturn(false);

        assertThatThrownBy(() -> menuBo.create(mockMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("없는 상품으로 메뉴 생성 시도 시 실패")
    @Test
    public void createMenuFailWithNonExistProduct() {
        given(menuGroupDao.existsById(1L)).willReturn(true);

        assertThatThrownBy(() -> menuBo.create(mockMenu)).isInstanceOf(IllegalArgumentException.class);
    }

    private void setupDefaultMenu() {
        mockMenu.setId(1L);
        mockMenu.setMenuGroupId(1L);
        mockMenu.setPrice(BigDecimal.valueOf(16000));
        mockMenu.setName("testMenu");
        mockMenu.setMenuProducts(mockMenuProducts);
    }

    private void setupDefaultMenuProduct() {
        mockMenuProduct.setSeq(1L);
        mockMenuProduct.setMenuId(1L);
        mockMenuProduct.setQuantity(10);
        mockMenuProduct.setProductId(2L);
    }

    private void setupDefaultProduct() {
        mockProduct.setId(2L);
        mockProduct.setName("testProduct");
        mockProduct.setPrice(BigDecimal.valueOf(16000));
    }
}
