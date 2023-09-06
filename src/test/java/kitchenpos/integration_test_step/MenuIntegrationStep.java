package kitchenpos.integration_test_step;

import kitchenpos.domain.*;
import kitchenpos.test_fixture.MenuGroupTestFixture;
import kitchenpos.test_fixture.MenuProductTestFixture;
import kitchenpos.test_fixture.MenuTestFixture;
import kitchenpos.test_fixture.ProductTestFixture;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

@Component
public class MenuIntegrationStep {
    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final ProductRepository productRepository;

    public MenuIntegrationStep(MenuRepository menuRepository, MenuGroupRepository menuGroupRepository, ProductRepository productRepository) {
        this.menuRepository = menuRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.productRepository = productRepository;
    }

    public Menu create() {
        Product product = ProductTestFixture.create()
                .getProduct();
        productRepository.save(product);
        return this.createPersistMenu(product);
    }

    public Menu createHideMenu() {
        MenuGroup menuGroup = MenuGroupTestFixture.create()
                .changeId(UUID.randomUUID())
                .getMenuGroup();
        menuGroupRepository.save(menuGroup);
        Product product = ProductTestFixture.create()
                .getProduct();
        productRepository.save(product);
        Menu menu = MenuTestFixture.create()
                .changeId(UUID.randomUUID())
                .changePrice(product.getPrice().multiply(BigDecimal.valueOf(2)))
                .changeMenuGroup(menuGroup)
                .changeMenuProducts(Collections.singletonList(MenuProductTestFixture.create()
                        .changeProduct(product)
                        .getMenuProduct()))
                .changeDisplayed(false)
                .getMenu();
        return menuRepository.save(menu);
    }

    public Menu createPersistMenu(Product persistProduct) {
        MenuGroup menuGroup = MenuGroupTestFixture.create()
                .changeId(UUID.randomUUID())
                .getMenuGroup();
        menuGroupRepository.save(menuGroup);
        return this.create(persistProduct, menuGroup);
    }

    public Menu create(Product persistProduct, MenuGroup persistMenuGroup) {
        menuGroupRepository.save(persistMenuGroup);
        MenuProduct menuProduct = MenuProductTestFixture.create()
                .changeProduct(persistProduct)
                .getMenuProduct();
        Menu menu = MenuTestFixture.create()
                .changeId(UUID.randomUUID())
                .changePrice(menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                .changeMenuGroup(persistMenuGroup)
                .changeMenuProducts(Collections.singletonList(menuProduct))
                .changeDisplayed(true)
                .getMenu();
        return menuRepository.save(menu);
    }
}
