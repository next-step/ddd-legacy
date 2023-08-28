package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kitchenpos.Fixtures.createMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class MenuGroupServiceTest {
    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Test
    @DisplayName("메뉴그룹을 등록한다.")
    void create01() {
        MenuGroup menuGroup = createMenuGroup("떡볶이그룹");

        MenuGroup savedMenuGroup = menuGroupService.create(menuGroup);

        MenuGroup findMenuGroup = menuGroupRepository.findById(savedMenuGroup.getId()).orElseThrow();
        assertThat(findMenuGroup.getId()).isEqualTo(savedMenuGroup.getId());
    }

    @Test
    @DisplayName("메뉴그룹 등록할 때 이름이 필요하다.")
    void create02() {
        MenuGroup menuGroup = createMenuGroup(null);

        assertThatThrownBy(() -> menuGroupService.create(menuGroup)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("등록된 메뉴그룹을 조회한다.")
    void findAll01() {
        MenuGroup menuGroup1 = createMenuGroup("떡볶이그룹");
        MenuGroup menuGroup2 = createMenuGroup("돈까스그룹");
        List<MenuGroup> savedMenuGroups = menuGroupRepository.saveAll(List.of(menuGroup1, menuGroup2));

        List<MenuGroup> menuGroups = menuGroupService.findAll();

        assertThat(menuGroups).hasSize(2);
        assertThat(menuGroups).extracting("id")
                              .containsExactly(savedMenuGroups.get(0).getId(), savedMenuGroups.get(1).getId());
    }
}
