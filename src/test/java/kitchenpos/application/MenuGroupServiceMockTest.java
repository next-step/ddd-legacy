package kitchenpos.application;

import kitchenpos.ApplicationMockTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static kitchenpos.fixture.MenuGroupFixture.menuGroupCreateRequest;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupResponse;
import static kitchenpos.fixture.MenuGroupFixture.이름_추천메뉴;
import static kitchenpos.fixture.MenuGroupFixture.이름_한마리메뉴;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("메뉴그룹 서비스 테스트")
@ApplicationMockTest
class MenuGroupServiceMockTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴그룹 등록한다.")
    @Nested
    class MenuGroupCreate {
        @DisplayName("[성공] 등록")
        @Test
        void success() {
            // given
            MenuGroup request = menuGroupCreateRequest(이름_추천메뉴);
            when(menuGroupRepository.save(any(MenuGroup.class))).thenReturn(menuGroupResponse(이름_추천메뉴));

            // when
            MenuGroup result = menuGroupService.create(request);

            // then
            assertAll(
                    () -> assertThat(result.getId()).isNotNull(),
                    () -> assertThat(result.getName()).isEqualTo(이름_추천메뉴)
            );
        }

        @DisplayName("[실패] 이름은 공백일 수 없다.")
        @NullAndEmptySource
        @ParameterizedTest
        void fail1(String name) {
            // given
            MenuGroup request = menuGroupCreateRequest(name);

            // when
            // then
            assertThatThrownBy(() -> menuGroupService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("메뉴그룹 목록을 볼 수 있다")
    @Nested
    class MenuGroupList {
        @DisplayName("[성공] 목록보기")
        @Test
        void getMenuGroups() {
            // given
            List<MenuGroup> 메뉴그룹_목록 = List.of(menuGroupResponse(이름_추천메뉴), menuGroupResponse(이름_한마리메뉴));
            when(menuGroupRepository.findAll()).thenReturn(메뉴그룹_목록);

            // when
            List<MenuGroup> result = menuGroupService.findAll();

            // then
            assertThat(result).extracting("name").containsExactly(이름_추천메뉴, 이름_한마리메뉴);
        }
    }
}
