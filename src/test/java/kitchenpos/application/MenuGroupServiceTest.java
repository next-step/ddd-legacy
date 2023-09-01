package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.spy.SpyMenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    private MenuGroupService menuGroupService;

    @Spy
    private SpyMenuGroupRepository menuGroupRepository;

    @BeforeEach
    void beforeEach() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹 등록")
    @Nested
    class CreateTestGroup {

        @DisplayName("메뉴 그룹의 이름이 없으면 예외 발생")
        @ParameterizedTest(name = "메뉴 그룹명: {0}")
        @NullAndEmptySource
        void createTest1(String name) {

            // given
            final MenuGroup request = MenuGroupFixture.createMenuGroup(name);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> menuGroupService.create(request));
        }

        @DisplayName("메뉴 그룹 등록 완료")
        @Test
        void createTest2() {

            // given
            final MenuGroup request = MenuGroupFixture.createMenuGroup();

            // when
            MenuGroup actual = menuGroupService.create(request);

            // then
            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getName()).isEqualTo(request.getName());
        }
    }

    @DisplayName("등록된 메뉴 그룹을 모두 조회")
    @Test
    void findAllTest() {

        // given
        final MenuGroup menuGroup = MenuGroupFixture.createMenuGroup();

        given(menuGroupRepository.findAll())
                .willReturn(List.of(menuGroup));

        // when
        List<MenuGroup> actual = menuGroupService.findAll();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.size()).isOne();
    }
}