package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static kitchenpos.fixture.MenuGroupFixture.menuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("메뉴 그룹 서비스 단위 테스트")
class MenuGroupServiceUnitTest {

    private MenuGroupRepository menuGroupRepository = mock(MenuGroupRepository.class);
    private MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);

    @BeforeEach
    void setUp() {
        menuGroupRepository = mock(MenuGroupRepository.class);
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹을 등록할 수 있습니다.")
    @ParameterizedTest(name = "메뉴 그룹명 : `{0}`")
    @ValueSource(strings = {"일반 메뉴", "세트 메뉴", "추천 메뉴", "사이드 메뉴", "기타"})
    void create(final String name) {
        when(menuGroupRepository.save(any(MenuGroup.class))).then(returnsFirstArg());

        final MenuGroup menuGroup = menuGroup(null, name);
        final MenuGroup actual = menuGroupService.create(menuGroup);
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(name)
        );
    }

    @DisplayName("메뉴 그룹명은 1자 이상이어야 합니다.")
    @ParameterizedTest(name = "메뉴 그룹명 : `{0}`")
    @NullAndEmptySource
    void createWithEmptyName(final String name) {
        final MenuGroup menuGroup = menuGroup(name);

        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
