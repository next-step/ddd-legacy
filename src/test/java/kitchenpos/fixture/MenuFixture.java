package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static class MenuBuilder {
        private UUID id;
        private String name;
        private BigDecimal price;
        private MenuGroup menuGroup;
        private Boolean displayed;
        private List<MenuProduct> menuProducts;
        private UUID menuGroupId;

        public MenuBuilder() {
            this.id = UUID.randomUUID();
        }

        public MenuBuilder name(String name) {
            this.name=name;
            return this;
        }

        public MenuBuilder price(BigDecimal price) {
            this.price=price;
            return this;
        }

        public MenuBuilder menuGroup(MenuGroup menuGroup) {
            this.menuGroup=menuGroup;
            return this;
        }

        public MenuBuilder displayed(Boolean displayed) {
            this.displayed=displayed;
            return this;
        }

        public MenuBuilder menuProducts(List<MenuProduct> menuProducts) {
            this.menuProducts=menuProducts;
            return this;
        }

        public MenuBuilder menuGroupId(UUID menuGroupId) {
            this.menuGroupId=menuGroupId;
            return this;
        }

        public Menu build() {
            Menu 메뉴 = new Menu();
            메뉴.setName(this.name);
            메뉴.setPrice(this.price);
            메뉴.setMenuGroup(this.menuGroup);
            메뉴.setDisplayed(this.displayed);
            메뉴.setMenuProducts(this.menuProducts);
            메뉴.setMenuGroupId(this.menuGroupId);
            return 메뉴;
        }
    }
}
