package kitchenpos.model;

public final class MenuGroupBuilder {
    private Long id;
    private String name;

    private MenuGroupBuilder() {}

    public static MenuGroupBuilder aMenuGroup() { return new MenuGroupBuilder(); }

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
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }
}
