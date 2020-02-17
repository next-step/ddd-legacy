package kitchenpos.menu.supports;

import java.util.Collections;
import java.util.List;

import kitchenpos.bo.MenuBo;
import kitchenpos.menu.group.supports.MenuGroupDaoWithCollection;
import kitchenpos.menu.product.supports.MenuProductWithCollection;
import kitchenpos.model.MenuGroup;
import kitchenpos.model.Product;
import kitchenpos.product.supports.ProductDaoWithCollection;
import kitchenpos.product.supports.ProductDaoWithConstraint;

public class MenuBoFactory {

    public static MenuBo withFixtures(List<Product> products, MenuGroup menuGroup) {
        return new MenuBo(new MenuDaoWithCollection(Collections.emptyList()),
                          new MenuGroupDaoWithCollection(Collections.singletonList(menuGroup)),
                          new MenuProductWithCollection(Collections.emptyList()),
                          new ProductDaoWithCollection(products));
    }

    public static MenuBo withFixtures(List<Product> products) {
        return new MenuBo(new MenuDaoWithCollection(Collections.emptyList()),
                          new MenuGroupDaoWithCollection(Collections.emptyList()),
                          new MenuProductWithCollection(Collections.emptyList()),
                          new ProductDaoWithCollection(products));
    }

    public static MenuBo withFixturesAndExternalConstraint(List<Product> products, MenuGroup menuGroup) {
        return new MenuBo(MenuDaoWithConstraint.withCollection(Collections.emptyList()),
                          new MenuGroupDaoWithCollection(Collections.singletonList(menuGroup)),
                          new MenuProductWithCollection(Collections.emptyList()),
                          ProductDaoWithConstraint.withCollection(products));
    }
}
