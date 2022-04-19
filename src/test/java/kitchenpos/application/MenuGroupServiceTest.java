package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * <pre>
 * kitchenpos.application
 *      MenuGroupServiceTest
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-22 오후 9:20
 */

@DisplayName("[메뉴 그룹]")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    @DisplayName("메뉴 그룹 이름값을 필수로 갖는다")
    public void requiredMenuGroupName() {
        MenuGroup menuGroup = new MenuGroup();
        AssertionsForClassTypes.assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴 그룹을 등록한다")
    public void createMenuGroup() {

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("세트 메뉴");

        given(menuGroupRepository.save(any())).willReturn(menuGroup);

        MenuGroup findMenuGroup = menuGroupService.create(menuGroup);

        assertThat(findMenuGroup.getId()).isEqualTo(menuGroup.getId());
        assertThat(findMenuGroup.getName()).isEqualTo(menuGroup.getName());
    }
//
//    @Test
//    @DisplayName("메뉴 그룹을 전체 조회할 수 있다.")
//    public void searchMenuGroupAll() {
//        List<MenuGroup> menuGroups = Arrays.asList(
//                MenuGroupFixture.메뉴_그룹(UUID.randomUUID(), "한마리 치킨"),
//                MenuGroupFixture.메뉴_그룹(UUID.randomUUID(), "두마리 치킨"),
//                MenuGroupFixture.메뉴_그룹(UUID.randomUUID(), "신 메뉴")
//        );
//
//        given(menuGroupRepository.findAll()).willReturn(menuGroups);
//        List<MenuGroup> searchMenuGroups = menuGroupService.findAll();
//        assertThat(searchMenuGroups.size()).isEqualTo(menuGroups.size());
//    }
}
