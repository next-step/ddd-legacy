package kitchenpos.setup;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.generateMenu;
import static kitchenpos.fixture.MenuGroupFixture.generateMenuGroup;
import static kitchenpos.fixture.ProductFixture.generateProduct;

@Component
public class MenuSetup {

    private final MenuRepository menuRepository;
    private final MenuGroupSetup menuGroupSetup;
    private final ProductSetup productSetup;

    public MenuSetup(MenuRepository menuRepository, MenuGroupSetup menuGroupSetup, ProductSetup productSetup) {
        this.menuRepository = menuRepository;
        this.menuGroupSetup = menuGroupSetup;
        this.productSetup = productSetup;
    }

    public Menu setupMenu(final Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu loadMenu(final UUID uuid) {
        return menuRepository.findById(uuid).get();
    }

    public Menu setupMenu() {
        final Product product = productSetup.setupProduct(generateProduct());
        final MenuGroup menuGroup = menuGroupSetup.setupMenuGroup(generateMenuGroup());
        final Menu menu = generateMenu(product, 1, menuGroup);

        return setupMenu(menu);
    }
}
