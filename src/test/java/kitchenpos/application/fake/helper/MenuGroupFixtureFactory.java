package kitchenpos.application.fake.helper;

import kitchenpos.domain.MenuGroup;

import java.util.UUID;

public final class MenuGroupFixtureFactory {

    public static final MenuGroup 런체세트그룹 = new Builder()
            .id(UUID.randomUUID())
            .name("런치세트그룹")
            .build();

    public static final class Builder implements FixtureBuilder<MenuGroup> {

        private UUID id;
        private String name;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public MenuGroup build() {
            MenuGroup group = new MenuGroup();
            group.setId(this.id);
            group.setName(this.name);
            return group;
        }

    }
}
