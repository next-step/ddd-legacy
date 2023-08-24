package kitchenpos.integration_test_step;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.test_fixture.MenuGroupTestFixture;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class MenuGroupIntegrationStep {
    private final MenuGroupRepository menuGroupRepository;

    public MenuGroupIntegrationStep(MenuGroupRepository menuGroupRepository) {
        this.menuGroupRepository = menuGroupRepository;
    }

    @Transactional
    public MenuGroup createPersistMenuGroup() {
        MenuGroup menuGroup = MenuGroupTestFixture.create()
                .changeId(UUID.randomUUID())
                .changeName("테스트 메뉴 그룹")
                .getMenuGroup();
        return menuGroupRepository.save(menuGroup);
    }
}
