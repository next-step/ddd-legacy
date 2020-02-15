package kitchenpos.bo;

import kitchenpos.model.MenuGroup;
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

    public static List<Product> products = Collections.emptyList();
    public static List<MenuGroup> menuGroups = Collections.emptyList();

    @BeforeAll
    public static void setUp() {
        final Product product = new Product();
        product.setId(1l);
        product.setName("짜장면");
        product.setPrice(BigDecimal.valueOf(6000));

        final Product product1 = new Product();
        product1.setId(2l);
        product1.setName("짬봉");
        product1.setPrice(BigDecimal.valueOf(7000));

        products = Arrays.asList(product, product1);


        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1l);
        menuGroup.setName("면 요리 세트");


        final MenuGroup menuGroup1 = new MenuGroup();
        menuGroup1.setId(2l);
        menuGroup1.setName("밥 요리 세트");

        menuGroups = Arrays.asList(menuGroup, menuGroup1);
    }
}
