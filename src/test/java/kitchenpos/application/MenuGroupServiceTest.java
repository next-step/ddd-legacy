package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.AdditionalAnswers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Autowired
    @MockBean
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴 그룹을 등록할 수 있습니다.")
    @ParameterizedTest(name = "입력값 `{0}`")
    @ValueSource(strings = {"일반 메뉴", "세트 메뉴", "추천 메뉴", "사이드 메뉴", "기타"})
    void create(final String name) {
        final MenuGroup menuGroup = MenuGroupFixture.menuGroup(null, name);
        when(menuGroupRepository.save(any(MenuGroup.class))).then(AdditionalAnswers.returnsFirstArg());

        final MenuGroup actual = menuGroupService.create(menuGroup);
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(name)
        );
    }

    @DisplayName("메뉴 그룹명은 1자 이상이어야 합니다.")
    @ParameterizedTest(name = "입력값 `{0}`")
    @NullAndEmptySource
    void createWithEmptyName(final String name) {
        final MenuGroup menuGroup = MenuGroupFixture.menuGroup(name);

        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
