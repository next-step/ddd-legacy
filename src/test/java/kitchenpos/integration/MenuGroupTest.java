package kitchenpos.integration;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.integration.annotation.TestAndRollback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MenuGroupTest extends IntegrationTestRunner {

    @Autowired
    private MenuGroupService menuGroupService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("메뉴 그룹을 생성한다 ( 메뉴 그룹 생성시 이름이 공백 일 수 없다. )")
    @Test
    public void create_with_empty_name() {
        //given
        MenuGroup request = new MenuGroup();
        request.setName("");

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuGroupService.create(request));
    }

    @DisplayName("메뉴 그룹을 생성한다 ( 메뉴 그룹 생성시 이름이 null 일 수 없다. )")
    @Test
    public void create_with_null_name() {
        //given
        MenuGroup request = new MenuGroup();

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuGroupService.create(request));
    }

    @DisplayName("메뉴 그룹을 생성한다")
    @TestAndRollback
    public void create() {
        //given
        final String menuGroupName = "추천 메뉴";
        MenuGroup request = new MenuGroup();
        request.setName(menuGroupName);

        //when
        final MenuGroup menuGroup = menuGroupService.create(request);
        final MenuGroup findMenuGroup = menuGroupRepository.findById(menuGroup.getId()).get();

        //then
        assertThat(findMenuGroup.getName()).isEqualTo(menuGroupName);
        assertThat(findMenuGroup.getId()).isNotNull();
    }

    @DisplayName("메뉴 그룹을 생성한다 ( 같은 이름의 메뉴 그룹을 생성 할 수 있다. )")
    @TestAndRollback
    public void create_with_same_name() {
        //given
        final String menuGroupName = "추천 메뉴";
        MenuGroup request = new MenuGroup();
        request.setName(menuGroupName);

        //when
        final MenuGroup menuGroup_1 = menuGroupService.create(request);
        final MenuGroup menuGroup_2 = menuGroupService.create(request);
        final MenuGroup findMenuGroup_1 = menuGroupRepository.findById(menuGroup_1.getId()).get();
        final MenuGroup findMenuGroup_2 = menuGroupRepository.findById(menuGroup_2.getId()).get();

        //then
        assertThat(findMenuGroup_1.getName()).isEqualTo(menuGroupName);
        assertThat(findMenuGroup_2.getName()).isEqualTo(menuGroupName);
        assertThat(findMenuGroup_1.getId()).isNotEqualTo(findMenuGroup_2.getId());
    }

    @TestAndRollback
    @DisplayName("모든 메뉴 그룹을 조회 한다.")
    public void findAll() {
        //given
        final String menuGroupName_1 = "추천 메뉴";
        final MenuGroup request_1 = new MenuGroup();
        request_1.setName(menuGroupName_1);

        final String menuGroupName_2 = "메인 메뉴";
        final MenuGroup request_2 = new MenuGroup();
        request_2.setName(menuGroupName_2);

        final String menuGroupName_3 = "메인 메뉴";
        MenuGroup request_3 = new MenuGroup();
        request_3.setName(menuGroupName_3);

        menuGroupService.create(request_1);
        menuGroupService.create(request_2);
        menuGroupService.create(request_3);

        //when
        final List<MenuGroup> menuGroups = menuGroupService.findAll();

        //then
        assertThat(menuGroups.size()).isEqualTo(3);
        assertThat(menuGroups.get(0).getName()).isEqualTo(menuGroupName_1);
        assertThat(menuGroups.get(1).getName()).isEqualTo(menuGroupName_2);
        assertThat(menuGroups.get(2).getName()).isEqualTo(menuGroupName_3);
    }

}
