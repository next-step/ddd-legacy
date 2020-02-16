package kitchenpos.builder;

import kitchenpos.model.MenuGroup;

public class MenuGroupBuilder {
    private Long id;
    private String name;

    public MenuGroupBuilder() {
    }

    public MenuGroupBuilder id(Long val) {
        id = val;
        return this;
    }

    public MenuGroupBuilder name(String val) {
        name = val;
        return this;
    }

    public MenuGroup build() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(this.id);
        menuGroup.setName(this.name);
        return menuGroup;
    }

}
