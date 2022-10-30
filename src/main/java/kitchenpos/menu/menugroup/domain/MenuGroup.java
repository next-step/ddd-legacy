package kitchenpos.menu.menugroup.domain;

import kitchenpos.common.vo.Name;

import javax.persistence.*;
import java.util.UUID;

@Table(name = "menu_group")
@Entity
public class MenuGroup {
    @Column(name = "id", columnDefinition = "binary(16)")
    @Id
    private UUID id;

    @Embedded
    private Name name;

    protected MenuGroup() {
    }

    public MenuGroup(UUID id, Name name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public Name getName() {
        return this.name;
    }

}
