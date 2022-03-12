package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuGroupFixture {

    public static class MenuGroupBuilder {
        private UUID id;
        private String name;

        public MenuGroupBuilder() {
            this.id = UUID.randomUUID();
        }

        public MenuGroupBuilder name(String name) {
            this.name=name;
            return this;
        }

        public MenuGroup build() {
            MenuGroup 메뉴그룹 = new MenuGroup();
            메뉴그룹.setName(this.name);
            return 메뉴그룹;
        }
    }
}
