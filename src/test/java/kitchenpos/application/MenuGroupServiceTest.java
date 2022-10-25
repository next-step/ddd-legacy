package kitchenpos.application;

import kitchenpos.common.vo.Name;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.menu.menugroup.application.MenuGroupService;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.menu.menugroup.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("메뉴 그룹 서비스")
class MenuGroupServiceTest {

    private MenuGroupService menuGroupService;
    private MenuGroupRepository menuGroupRepository;
    private PurgomalumClient purgomalumClient;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        purgomalumClient = new FakePurgomalumClient();
        menuGroupService = new MenuGroupService(menuGroupRepository, purgomalumClient);
    }

    @Test
    void name() {
        menuGroupRepository.save(new MenuGroup(UUID.randomUUID(), new Name("메뉴그룹명", false)));
        assertThat(menuGroupService.findAll()).hasSize(1);
    }
}
