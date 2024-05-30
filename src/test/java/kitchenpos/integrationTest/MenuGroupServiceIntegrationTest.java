package kitchenpos.integrationTest;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.repository.InMemoryMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceIntegrationTest {
    private MenuGroupRepository menuGroupRepository;
    private MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupRepository = new InMemoryMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @Test
    void 메뉴그룹을_등록_할_수_있다() {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("치킨메뉴");
        final MenuGroup actual = menuGroupService.create(menuGroup);
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("치킨메뉴");
    }

    @Test
    void 이름이_없으면_메뉴그룹_등록_실패() {
        final MenuGroup menuGroup = new MenuGroup();
        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 모든_메뉴그룹을_조회할_수_있다() {
        final MenuGroup menuGroup1 = new MenuGroup();
        menuGroup1.setName("치킨메뉴");
        final MenuGroup menuGroup2 = new MenuGroup();
        menuGroup2.setName("피자메뉴");
        menuGroupService.create(menuGroup1);
        menuGroupService.create(menuGroup2);

        final List<MenuGroup> menuGroups = menuGroupService.findAll();
        assertThat(menuGroups).hasSize(2);
    }
}
