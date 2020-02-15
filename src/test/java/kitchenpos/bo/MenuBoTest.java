package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.model.Menu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MenuBoTest {

    @Mock
    private MenuDao menuDao;
    @Mock
    private MenuGroupDao menuGroupDao;

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
    @DisplayName("같은 카테고리를 가질 수 없다.")
    void createMenuByValidationMenuGroup() {
        // give
        given(menuGroupDao.existsById(2L))
                .willReturn(true);
        // when then
        assertThatIllegalArgumentException().isThrownBy(() -> menuBo.create(menu));
    }

}
