package kitchenpos.setup;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class MenuSetup {

    private final MenuRepository menuRepository;

    public MenuSetup(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public Menu setupMenu(final Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu loadMenu(final UUID uuid) {
        return menuRepository.findById(uuid).get();
    }
}
