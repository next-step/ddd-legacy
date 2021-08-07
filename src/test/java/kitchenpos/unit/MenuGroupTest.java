package kitchenpos.unit;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MenuGroupTest extends UnitTestRunner {

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("메뉴 그룹을 생성한다.")
    @Test
    public void create() {
        //given
        final String menuName = "메인 메뉴";
        final MenuGroup request = new MenuGroup();
        request.setName(menuName);

        final MenuGroup stubbedMenuGroup = new MenuGroup();
        stubbedMenuGroup.setName(menuName);
        stubbedMenuGroup.setId(UUID.randomUUID());

        when(menuGroupRepository.save(any(MenuGroup.class))).thenReturn(stubbedMenuGroup);

        //when
        final MenuGroup menuGroup = menuGroupService.create(request);

        //then
        assertThat(menuGroup.getId()).isNotNull();
        assertThat(menuGroup.getName()).isEqualTo(menuName);

    }

    @DisplayName("모든 메뉴 그룹을 조회 한다.")
    @Test
    public void findAll() {
        //given
        final String menuName_1 = "메인 메뉴";
        final String menuName_2 = "추천 메뉴";
        final String menuName_3 = "할인 메뉴";

        final MenuGroup stubbedMenuGroup_1 = new MenuGroup();
        stubbedMenuGroup_1.setName(menuName_1);
        stubbedMenuGroup_1.setId(UUID.randomUUID());

        final MenuGroup stubbedMenuGroup_2 = new MenuGroup();
        stubbedMenuGroup_2.setName(menuName_2);
        stubbedMenuGroup_2.setId(UUID.randomUUID());

        final MenuGroup stubbedMenuGroup_3 = new MenuGroup();
        stubbedMenuGroup_3.setName(menuName_3);
        stubbedMenuGroup_3.setId(UUID.randomUUID());

        when(menuGroupRepository.findAll()).thenReturn(List.of(stubbedMenuGroup_1, stubbedMenuGroup_2, stubbedMenuGroup_3));

        //when
        final List<MenuGroup> menuGroups = menuGroupService.findAll();

        //then
        assertThat(menuGroups.size()).isEqualTo(3);
        assertThat(menuGroups.get(0)).isEqualTo(stubbedMenuGroup_1);
        assertThat(menuGroups.get(1)).isEqualTo(stubbedMenuGroup_2);
        assertThat(menuGroups.get(2)).isEqualTo(stubbedMenuGroup_3);


    }
}
