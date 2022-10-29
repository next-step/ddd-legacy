package kitchenpos.menu.menu.application;

import kitchenpos.common.vo.Price;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuRepository;
import kitchenpos.menu.menu.dto.request.ChangeMenuPriceRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ChangeMenuPriceService {
    private final MenuRepository menuRepository;

    public ChangeMenuPriceService(final MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Transactional
    public Menu changePrice(final UUID menuId, final ChangeMenuPriceRequest request) {
        final Menu menu = menuRepository.findById(menuId)
                .orElseThrow(NoSuchElementException::new);
        menu.changePrice(new Price(request.getPrice()));
        return menu;
    }

}
