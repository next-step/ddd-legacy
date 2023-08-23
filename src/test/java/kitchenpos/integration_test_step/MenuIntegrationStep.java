package kitchenpos.integration_test_step;

import kitchenpos.domain.*;
import kitchenpos.test_fixture.MenuGroupTestFixture;
import kitchenpos.test_fixture.MenuProductTestFixture;
import kitchenpos.test_fixture.MenuTestFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MenuIntegrationStep {
    private MenuIntegrationStep() {}

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

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
                .changePrice(BigDecimal.valueOf(2000))
                .changeMenuGroup(menuGroup)
                .changeMenuProducts(menuProduct)
                .changeDisplayed(true)
                .getMenu();
        return menuRepository.save(menu);
    }
}
