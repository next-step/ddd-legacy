package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.model.Menu;
import kitchenpos.model.MenuProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MenuBoTest {

    @Mock
    private MenuDao menuDao;
    @Mock
    private MenuGroupDao menuGroupDao;
    @Mock
    private ProductDao productDao;

    @InjectMocks
    private MenuBo menuBo;

    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = new Menu();
        menu.setId(1L);
        menu.setName("고기");
        menu.setPrice(BigDecimal.valueOf(1000));
        menu.setMenuGroupId(2L);
        menu.setMenuProducts(Collections.emptyList());
    }

    @Test
    @DisplayName("메뉴 가격은 0원 이하일 때")
    void createMenuByValidationPrice() {
        // give
        Menu menuActual = new Menu();
        menuActual.setPrice(BigDecimal.valueOf(0));
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> menuBo.create(menuActual));
    }

    @Test
    @DisplayName("등록된 메뉴 그룹에만 메뉴를 등록할 수 있다.")
    void createMenuByValidationMenuGroup() {
        // give
        given(menuGroupDao.existsById(2L))
                .willReturn(false);
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> menuBo.create(menu));
    }

    @Test
    @DisplayName("등록된 상품만 선택이 가능하다.")
    void createMenuByValidationProduct() {
        // give
        given(menuGroupDao.existsById(2L))
                .willReturn(true);
        menu.setPrice(BigDecimal.valueOf(0));
        menu.setMenuProducts(Arrays.asList(new MenuProduct()));
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> menuBo.create(menu));
    }
}
