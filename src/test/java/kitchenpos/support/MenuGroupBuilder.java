package kitchenpos.support;

import kitchenpos.model.MenuGroup;

public class MenuGroupBuilder {

    private Long id;
    private String name;

    public MenuGroupBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public MenuGroupBuilder name(String name) {
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
