package kitchenpos.commons;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuGroupGenerator {
    @Autowired
    private MenuGroupService menuGroupService;

    public MenuGroup generate() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("menu group 1");
        return menuGroupService.create(menuGroup);
    }
}
