package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
        setupDefaultMenu();
        setupDefaultMenuProduct();
        setupDefaultProduct();
        mockMenus.add(mockMenu);
        mockMenuProducts.add(mockMenuProduct);
    }

    private void setupDefaultMenu() {
        // Menu { id: 1L, menuGroupId: 1L, price: 16000, name: testMenu, menuProducts: mockMenuProducts }
        mockMenu.setId(1L);
        mockMenu.setMenuGroupId(1L);
        mockMenu.setPrice(BigDecimal.valueOf(16000));
        mockMenu.setName("testMenu");
        mockMenu.setMenuProducts(mockMenuProducts);
    }

    private void setupDefaultMenuProduct() {
        // MenuProduct { id: 1L, menuId: 1L, quantity: 100, productId: 2L }
        mockMenuProduct.setSeq(1L);
        mockMenuProduct.setMenuId(1L);
        mockMenuProduct.setQuantity(100);
        mockMenuProduct.setProductId(2L);
    }

    private void setupDefaultProduct() {
        // Product { id: 2L, name: testProduct, price: 16000 }
        mockProduct.setId(2L);
        mockProduct.setName("testProduct");
        mockProduct.setPrice(BigDecimal.valueOf(16000));
    }
}
