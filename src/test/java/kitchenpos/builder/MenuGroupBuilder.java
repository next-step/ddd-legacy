package kitchenpos.builder;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public final class MenuGroupBuilder {
    private UUID id;
    private String name;

    private MenuGroupBuilder() {
        id = UUID.randomUUID();
        name = "추천메뉴";
    }

    public static MenuGroupBuilder newInstance() {
        return new MenuGroupBuilder();
    }

    public MenuGroupBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public MenuGroupBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MenuGroup build() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }
}
