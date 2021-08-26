package kitchenpos.ui.dto;

import java.util.UUID;
import kitchenpos.domain.MenuGroup;

public class MenuGroupRequest {

    private UUID id;
    private String name;

    public MenuGroupRequest() {
    }

    public MenuGroupRequest(final String name) {
        this.name = name;
    }

    public MenuGroupRequest(final MenuGroup menuGroup) {
        this.name = menuGroup.getName();
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
