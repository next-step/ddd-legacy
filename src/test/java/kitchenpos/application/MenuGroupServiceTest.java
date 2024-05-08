package kitchenpos.application;

import kitchenpos.ApplicationMockTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroupRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("메뉴그룹 서비스 테스트")
@ApplicationMockTest
class MenuGroupServiceTest {
    private static final String 추천메뉴 = "추천메뉴";
    private static final String 한마리메뉴 = "한마리메뉴";
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴그룹을 등록한다")
    @Test
    void creatMenuGroup() {
        // given
        MenuGroup request = createMenuGroupRequest(추천메뉴);
        when(menuGroupRepository.save(any(MenuGroup.class))).thenReturn(createMenuGroup(추천메뉴));

        // when
        MenuGroup createdMenuGroup = menuGroupService.create(request);

        // then
        assertThat(createdMenuGroup.getId()).isNotNull();
        assertThat(createdMenuGroup.getName()).isEqualTo(request.getName());
    }

    @DisplayName("메뉴그룹을 등록할 때, 이름이 공백이면 예외가 발생한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void creatMenuGroup_nullOrEmptyNameException(String name) {
        // given
        MenuGroup request = createMenuGroupRequest(name);

        // when
        // then
        assertThatThrownBy(() -> menuGroupService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴그룹 목록을 볼 수 있다")
    @Test
    void getMenuGroups() {
        // given
        List<MenuGroup> menuGroups = List.of(createMenuGroup(추천메뉴), createMenuGroup(한마리메뉴));

        when(menuGroupRepository.findAll()).thenReturn(menuGroups);

        // when
        List<MenuGroup> foundMenuGroups = menuGroupService.findAll();

        // then
        assertThat(foundMenuGroups).hasSize(2);
    }
}
