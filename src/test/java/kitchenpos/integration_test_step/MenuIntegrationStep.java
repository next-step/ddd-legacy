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

    public Menu createPersistMenu(Product product) {
        MenuGroup menuGroup = MenuGroupTestFixture.create()
                .changeId(UUID.randomUUID())
                .getMenuGroup();
        menuGroupRepository.save(menuGroup);
        MenuProduct menuProduct = MenuProductTestFixture.create()
                .changeProduct(product)
                .getMenuProduct();
        Menu menu = MenuTestFixture.create()
                .changeId(UUID.randomUUID())
                .changePrice(menuProduct.getProduct().getPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                .changeMenuGroup(menuGroup)
                .changeMenuProducts(Collections.singletonList(menuProduct))
                .changeDisplayed(true)
                .getMenu();
        return menuRepository.save(menu);
    }
}
