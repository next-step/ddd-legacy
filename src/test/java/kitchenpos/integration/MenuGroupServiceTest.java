package kitchenpos.integration;

import kitchenpos.application.MenuGroupService;
import kitchenpos.integration.mock.FakeMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static kitchenpos.unit.Fixtures.aMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import(TestConfig.class)
class MenuGroupServiceTest {

    @Autowired
    MenuGroupService menuGroupService;

    @Autowired
    FakeMenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        menuGroupRepository.clear();
    }

    @DisplayName("메뉴 그룹을 생성한다.")
    @Test
    void create() {
        menuGroupService.create(aMenuGroup());

        assertThat(menuGroupService.findAll()).hasSize(1);
    }
}
