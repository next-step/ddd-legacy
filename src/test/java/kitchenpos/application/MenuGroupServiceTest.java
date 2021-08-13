package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MenuGroupServiceTest {

    @Autowired
    MenuGroupService menuGroupService;

    @Autowired
    MenuGroupRepository menuGroupRepository;

    @BeforeEach
    void setUp() {
        menuGroupRepository.deleteAll();
    }

    @DisplayName("메뉴 그룹 추가 실패 - 이름이 null")
    @Test
    void not_created_null_name() {
        //given
        MenuGroup newMenuGroup = new MenuGroup();
        UUID newId = UUID.randomUUID();

        newMenuGroup.setId(newId);

        //when & then
        assertThatThrownBy(() -> menuGroupService.create(newMenuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹 추가 실패 - 이름이 비어있다.")
    @Test
    void not_created_empty_name() {
        //given
        MenuGroup newMenuGroup = new MenuGroup();
        UUID newId = UUID.randomUUID();

        newMenuGroup.setId(newId);
        newMenuGroup.setName("");

        //when & then
        assertThatThrownBy(() -> menuGroupService.create(newMenuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("새로운 메뉴 그룹을 추가")
    @Test
    void create() {
        //given
        MenuGroup newMenuGroup = new MenuGroup();
        String newMenuGroupName = "새 메뉴 그룹";

        newMenuGroup.setName(newMenuGroupName);

        //when
        MenuGroup savedMenuGroup = menuGroupService.create(newMenuGroup);

        //then
        Optional<MenuGroup> findMenuGroup = menuGroupRepository.findById(savedMenuGroup.getId());
        assertThat(findMenuGroup).isPresent();
        assertThat(findMenuGroup.get()).isEqualTo(savedMenuGroup);
        assertThat(findMenuGroup.get().getName()).isEqualTo(savedMenuGroup.getName());
        assertThat(findMenuGroup.get().getId()).isEqualTo(savedMenuGroup.getId());
    }

    @DisplayName("모든 메뉴 그룹을 조회")
    @Test
    void findAll() {
        //given
        final String name01 = "메인 메뉴";
        final String name02 = "추천 메뉴";

        MenuGroup menuGroup01 = new MenuGroup();
        menuGroup01.setName(name01);
        MenuGroup menuGroup02 = new MenuGroup();
        menuGroup02.setName(name02);

        menuGroupService.create(menuGroup01);
        menuGroupService.create(menuGroup02);

        //when
        List<MenuGroup> findMenuGroups = menuGroupService.findAll();

        //then
        assertThat(findMenuGroups).hasSize(2);
        assertThat(findMenuGroups.get(0).getName()).isEqualTo(name01);
        assertThat(findMenuGroups.get(1).getName()).isEqualTo(name02);
    }
}
