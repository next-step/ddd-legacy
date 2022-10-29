package kitchenpos.menu.menu.application;

import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuProduct;
import kitchenpos.menu.menu.domain.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class MenuDisplayService {
    private final MenuRepository menuRepository;

    public MenuDisplayService(final MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Transactional
    public Menu display(final UUID menuId) {
        final Menu menu = menuRepository.findById(menuId)
                .orElseThrow(NoSuchElementException::new);
        menu.display();
        return menu;
    }

    @Transactional
    public Menu hide(final UUID menuId) {
        final Menu menu = menuRepository.findById(menuId)
                .orElseThrow(NoSuchElementException::new);
        menu.hide();
        return menu;
    }
}
