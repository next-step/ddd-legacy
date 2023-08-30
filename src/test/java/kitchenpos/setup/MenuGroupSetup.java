package kitchenpos.setup;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class MenuGroupSetup {

    private final MenuGroupRepository menuGroupRepository;

    public MenuGroupSetup(ProductRepository productRepository, MenuGroupRepository menuGroupRepository) {
        this.menuGroupRepository = menuGroupRepository;
    }

    public MenuGroup setupMenuGroup(final MenuGroup menuGroup) {
        return menuGroupRepository.save(menuGroup);
    }
}
