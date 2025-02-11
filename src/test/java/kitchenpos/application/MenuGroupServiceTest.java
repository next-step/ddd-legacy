package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static kitchenpos.fixture.TestFixture.makeTestMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    private final MenuGroupRepository menuGroupRepository= mock(MenuGroupRepository.class);
    private final MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);

    @DisplayName("메뉴 모음을 등록할 수 있다.")
    @ValueSource(strings = {"추천 메뉴", "오늘의 메뉴"})
    @ParameterizedTest
    void create(String name) {
        // given
        MenuGroup menuGroup = makeTestMenuGroup(name);
        when(menuGroupRepository.save(any(MenuGroup.class))).thenReturn(menuGroup);

        // when
        MenuGroup resultMenuGroup = menuGroupService.create(menuGroup);

        // then
        assertThat(resultMenuGroup.getId()).isNotNull();
        assertThat(resultMenuGroup.getName()).isEqualTo(menuGroup.getName());
    }

    @DisplayName("메뉴 모음명은 비어있다면 에러를 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    void nullName(String name) {
        // given
        MenuGroup menuGroup = makeTestMenuGroup(name);
        when(menuGroupRepository.save(any(MenuGroup.class))).thenReturn(menuGroup);

        // then
        assertThatThrownBy(() -> menuGroupService.create(menuGroup)).isInstanceOf(IllegalArgumentException.class);
    }
}
