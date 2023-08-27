package kitchenpos.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import javax.validation.constraints.Null;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.fixture.MenuFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MenuServiceTest {
    @Autowired
    MenuService menuService;
    @Autowired
    MenuGroupRepository menuGroupRepository;

    @Autowired
    ProductService productService;

    private Menu menu;
    private MenuGroup menuGroup;
    private List<Product> Products;

    @BeforeEach
    void setUp() {

    }


    @DisplayName("메뉴 생성 금액 체크")
    @Test
    public void 메뉴생성_금액체크() throws Exception {
        menu = MenuFixture.createDefaultWithNameAndPrice("가격체크메뉴",BigDecimal.valueOf(-10000));
        assertThrows(IllegalArgumentException.class, () -> {
            menuService.create(menu);
        });
    }
    
    @DisplayName("메뉴 상품 필수 값 체크")
    @Test
    public void 메뉴생성_메뉴그룹체크() throws Exception {
        menu = MenuFixture.createDefaultWithNameAndPrice("가격체크메",BigDecimal.valueOf(10000));
        assertThatThrownBy(
            () -> menuService.create(menu)
        ).isInstanceOf(NoSuchElementException.class);
    }
    
    

}