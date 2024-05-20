package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴그룹을 생성한다.")
    @Test
    void create() {
        // given
        final MenuGroup request = createMenuGroupRequest("후라이드 세트");
        final MenuGroup response = createMenuGroup("후라이드 세트");

        // when
        given(menuGroupRepository.save(any())).willReturn(response);
        MenuGroup actual = menuGroupService.create(request);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo(request.getName());
    }

    @DisplayName("메뉴 그룹의 이름이 올바르지 않으면 등록할 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void create_NameIsNullOrEmpty(final String name) {
        // given
        final MenuGroup menuGroupRequest = createMenuGroupRequest(name);

        // when then
        assertThatThrownBy(() -> menuGroupService.create(menuGroupRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록한 메뉴그룹의 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        MenuGroup menuGroup1 = createMenuGroup("후라이드 세트");
        MenuGroup menuGroup2 = createMenuGroup("양념치킨 세트");
        List<MenuGroup> menuGroups = Arrays.asList(menuGroup1, menuGroup2);

        given(menuGroupRepository.findAll()).willReturn(menuGroups);

        // when
        List<MenuGroup> actual = menuGroupService.findAll();

        // that
        assertThat(actual).hasSize(2)
                .extracting(MenuGroup::getName)
                .containsExactlyInAnyOrder("후라이드 세트", "양념치킨 세트");
    }

    private static MenuGroup createMenuGroupRequest(final String name) {
        MenuGroup request = new MenuGroup();
        request.setName(name);
        return request;
    }

    private static MenuGroup createMenuGroup(final String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

}
