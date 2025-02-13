package kitchenpos.util;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupBuilder {

    private UUID id;
    private String name;

    private MenuGroupBuilder() {
    }

    public static MenuGroupBuilder name(String name) {
        MenuGroupBuilder menuGroupBuilder = new MenuGroupBuilder();
        menuGroupBuilder.name = name;
        return menuGroupBuilder;
    }

    public MenuGroupBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public MenuGroup build() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }
}
