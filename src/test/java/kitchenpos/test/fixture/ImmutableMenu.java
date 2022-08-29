package kitchenpos.test.fixture;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

class ImmutableMenu extends Menu {

    ImmutableMenu(List<MenuProduct> menuProduct) {
        super.setName("후라이드 1개");
        super.setDisplayed(Boolean.TRUE);
        super.setPrice(BigDecimal.valueOf(15_000));
        super.setMenuProducts(menuProduct);
    }

    @Override
    public void setId(UUID id) {
        throw new IllegalAccessError();
    }

    @Override
    public void setName(String name) {
        throw new IllegalAccessError();
    }

    @Override
    public void setPrice(BigDecimal price) {
        throw new IllegalAccessError();
    }

    @Override
    public void setMenuGroup(MenuGroup menuGroup) {
        throw new IllegalAccessError();
    }

    @Override
    public void setDisplayed(boolean displayed) {
        throw new IllegalAccessError();
    }

    @Override
    public void setMenuProducts(List<MenuProduct> menuProducts) {
        throw new IllegalAccessError();
    }

    @Override
    public void setMenuGroupId(UUID menuGroupId) {
        throw new IllegalAccessError();
    }
}
