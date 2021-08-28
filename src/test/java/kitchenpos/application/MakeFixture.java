package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class MakeFixture {

    public static Menu createMenuTestFixture() {
        Product product = new Product();
        product.setId(UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10"));
        product.setName("테스트상품");
        product.setPrice(BigDecimal.valueOf(16000));

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.fromString("d9bc21ac-cc10-4593-b506-4a40e0170e02"));
        menuGroup.setName("테스트메뉴그룹");

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);

        Menu menu = new Menu();
        menu.setMenuGroupId(menuGroup.getId());
        menu.setName("메뉴1");
        menu.setPrice(BigDecimal.valueOf(12000));
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(Objects.requireNonNull(menuGroup).getId());
        menu.setMenuProducts(Collections.singletonList(menuProduct));

        return menu;
    }
}
