package kitchenpos.builder;

import kitchenpos.model.MenuGroup;

public class MenuGroupBuilder {
    private Long id;
    private String name;

    private MenuGroupBuilder() {
    }

    public static MenuGroupBuilder menuGroup() {
        return new MenuGroupBuilder();
    }

    public MenuGroupBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public MenuGroupBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MenuGroup build() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(this.id);
        menuGroup.setName(this.name);
        return menuGroup;
    }
}
