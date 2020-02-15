package kitchenpos.bo;

import kitchenpos.model.Menu;
import kitchenpos.model.MenuGroup;
import kitchenpos.model.MenuProduct;
import kitchenpos.model.Product;
import org.junit.jupiter.api.BeforeAll;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Geonguk Han
 * @since 2020-02-15
 */
public class Fixtures {

    public static final long FIRST_ID = 1l;
    public static final long SECOND_ID = 2l;
    public static final int QUANTITY_ONE = 1;

    public static List<Product> products = Collections.emptyList();
    public static List<MenuGroup> menuGroups = Collections.emptyList();
    public static Menu menu;
    public static List<MenuProduct> menuProducts = Collections.emptyList();

    @BeforeAll
    public static void setUp() {
        /* 상품 등록 */
        final Product product = new Product();
        product.setId(FIRST_ID);
        product.setName("짜장면");
        product.setPrice(BigDecimal.valueOf(6000));

        final Product product1 = new Product();
        product1.setId(FIRST_ID);
        product1.setName("짬봉");
        product1.setPrice(BigDecimal.valueOf(7000));

        products = Arrays.asList(product, product1);

        /* 메뉴 그룹 등록 */
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(FIRST_ID);
        menuGroup.setName("면 요리 세트");

        final MenuGroup menuGroup1 = new MenuGroup();
        menuGroup1.setId(SECOND_ID);
        menuGroup1.setName("밥 요리 세트");

        menuGroups = Arrays.asList(menuGroup, menuGroup1);

        /* 메뉴 등록 */
        final Menu newMenu = new Menu();
        newMenu.setId(FIRST_ID);
        newMenu.setName("메뉴");
        newMenu.setPrice(product.getPrice().add(product1.getPrice()));
        newMenu.setMenuGroupId(menuGroups.get(0).getId());

        menu = newMenu;

        /* 메뉴 상품 등록 */
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(FIRST_ID);
        menuProduct.setMenuId(FIRST_ID);
        menuProduct.setProductId(FIRST_ID);
        menuProduct.setQuantity(QUANTITY_ONE);

        final MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setSeq(SECOND_ID);
        menuProduct1.setMenuId(FIRST_ID);
        menuProduct1.setProductId(SECOND_ID);
        menuProduct1.setQuantity(QUANTITY_ONE);

        menuProducts = Arrays.asList(menuProduct, menuProduct1);
    }
}
