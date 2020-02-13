package kitchenpos.support;

import kitchenpos.model.MenuGroup;

public class MenuGroupBuilder {
    private long id;
    private String name;

    private MenuGroupBuilder() {
    }

    public static MenuGroupBuilder menuGroup() {
        return new MenuGroupBuilder();
    }

    public MenuGroupBuilder withId(final long id) {
        this.id = id;
        return this;
    }

    public MenuGroupBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public MenuGroup build() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

}
