package kitchenpos.domain;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

    public MenuGroup(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MenuGroup))
            return false;
        MenuGroup menuGroup = (MenuGroup)o;
        return Objects.equals(id, menuGroup.id) && Objects.equals(name, menuGroup.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
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
}
