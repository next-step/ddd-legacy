package kitchenpos.fixture;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupFixture {

    public static class MenuGroupBuilder {
        private final UUID id;
        private String name;

        public MenuGroupBuilder() {
            this.id = UUID.randomUUID();
        }

        public MenuGroupBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MenuGroup build() {
            MenuGroup 메뉴그룹 = new MenuGroup();
            메뉴그룹.setName(this.name);
            return 메뉴그룹;
        }
    }
}
