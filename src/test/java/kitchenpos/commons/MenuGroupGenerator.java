package kitchenpos.commons;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MenuGroupGenerator {
    @Autowired
    private MenuGroupService menuGroupService;

    public MenuGroup generate() {
        MenuGroup menuGroup = this.generateRequest();
        return menuGroupService.create(menuGroup);
    }

    public MenuGroup generateRequest() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("menu group 1");
        return menuGroup;
    }
}
