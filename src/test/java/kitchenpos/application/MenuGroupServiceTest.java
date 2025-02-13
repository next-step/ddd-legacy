package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.util.MenuGroupBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @DisplayName("메뉴 그룹 목록 조회")
    @Nested
    class FindAllMenuGroups {

        @DisplayName("메뉴 그룹 목록 조회 성공하면 메뉴 그룹 목록을 반환한다.")
        @Test
        void if_success_then_return_menu_groups() {
            // given
            given(menuGroupRepository.findAll())
                    .willReturn(List.of(new MenuGroup(), new MenuGroup()));

            // when
            var menuGroups = menuGroupService.findAll();

            // then
            assertThat(menuGroups).hasSize(2);
        }
    }

    @DisplayName("메뉴 그룹 생성")
    @Nested
    class MenuGroupCreate {

        @DisplayName("메뉴 그룹 이름이 null이거나 빈 문자열인 경우 예외를 던진다.")
        @ParameterizedTest
        @NullAndEmptySource
        void if_name_is_null_or_empty_then_throw_exception(String name) {
            // given
            var request = MenuGroupBuilder.name(name).build();

            // when
            assertThatThrownBy(() -> menuGroupService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴 그룹 생성 성공하면 메뉴 그룹을 반환한다.")
        @ParameterizedTest
        @ValueSource(strings = {"치킨", "사이드 메뉴", "음료"})
        void if_success_then_return_menu_group(String name) {
            // given
            var request = MenuGroupBuilder.name(name).build();
            given(menuGroupRepository.save(any(MenuGroup.class)))
                    .will(invocation -> invocation.getArgument(0));

            // when
            var menuGroup = menuGroupService.create(request);

            // then
            assertThat(menuGroup).isNotNull();
            assertThat(menuGroup.getName()).isEqualTo(name);
        }
    }
}
