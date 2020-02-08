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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuBoTest {
    @InjectMocks private MenuBo menuBo;
    @Mock private MenuDao menuDao;
    @Mock private MenuProductDao menuProductDao;


    @DisplayName("각 메뉴에 이름,가격을 설정할 수 있다.")
    @Test
    public void create() {

    }

    @DisplayName("메뉴 목록을 볼 수 있다.")
    @Test
    public void list() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(1L);
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(3L);
        menuProduct.setSeq(1L);
        List<MenuProduct> menuProductList = new ArrayList<>();
        menuProductList.add(menuProduct);

        Menu menu = new Menu();
        menu.setName("test");
        menu.setPrice(new BigDecimal(16000));
        menu.setMenuProducts(menuProductList);
        menu.setMenuGroupId(1L);
        menu.setId(1L);
        List<Menu> menuList = new ArrayList<>();
        menuList.add(menu);
        when(menuDao.findAll()).thenReturn(menuList);
        when(menuProductDao.findAllByMenuId(1L)).thenReturn(menuProductList);

        List<Menu> result = menuBo.list();

        assertThat(result.get(0).getName()).isEqualTo("test");
    }
}