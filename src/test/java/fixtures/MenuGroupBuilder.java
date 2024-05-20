package fixtures;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public class MenuGroupBuilder {

    private UUID id = UUID.randomUUID();
    private String name = "menu group name";

    public MenuGroupBuilder withName(String name) {
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
