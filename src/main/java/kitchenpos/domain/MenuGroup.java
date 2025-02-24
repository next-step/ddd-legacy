package kitchenpos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Table(name = "menu_group")
@Entity
public class MenuGroup {
    @Column(name = "id", columnDefinition = "binary(16)")
    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    public MenuGroup() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final MenuGroup menuGroup;

        public Builder() {
            menuGroup = new MenuGroup();
        }

        public Builder id(UUID id) {
            menuGroup.id = id;
            return this;
        }

        public Builder name(String name) {
            menuGroup.name = name;
            return this;
        }

        public MenuGroup build() {
            return menuGroup;
        }
    }
}
