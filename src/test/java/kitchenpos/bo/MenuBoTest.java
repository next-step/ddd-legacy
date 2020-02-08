package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuGroup;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {
    @Mock
    private Menu menu;

    @Mock
    private MenuGroup menuGroup;

    @Mock
    private MenuProduct menuProduct;

    @Mock
    private Product product;

    @InjectMocks
    private MenuBo menuBo;

    @InjectMocks
    private MenuDao menuDao;

    @InjectMocks
    private MenuGroupDao menuGroupDao;

    @InjectMocks
    private ProductDao productDao;


    @DisplayName("각 메뉴에 이름,가격을 설정할 수 있다.")
    @Test
    public void create() {

    }

    @DisplayName("메뉴 목록을 볼 수 있다.")
    @Test
    public void list() {
        MockitoAnnotations.initMocks(this);
        assertTrue(menu != null);
        assertTrue(menuGroup != null);
        assertTrue(menuProduct != null);
        assertTrue(product != null);
        assertTrue(menuBo != null);
        assertTrue(menuDao != null);
        assertTrue(menuGroupDao != null);
        assertTrue(productDao != null);


    }
}