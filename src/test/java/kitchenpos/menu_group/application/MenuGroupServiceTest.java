package kitchenpos.menu_group.application;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.menu_group.fixture.MenuGroupFixture.메뉴_그룹;
import static kitchenpos.menu_group.fixture.MenuGroupFixture.메뉴_그룹_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("MenuGroup 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    MenuGroupRepository menuGroupRepository;

    MenuGroupService menuGroupService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹을 등록한다")
    @ParameterizedTest
    @ValueSource(strings = {"한마리메뉴","두마리메뉴"})
    public void create(String name) {
        // given
        MenuGroup 메뉴_그룹_요청 = 메뉴_그룹_요청(name);
        MenuGroup 메뉴_그룹 = 메뉴_그룹(name);
        given(menuGroupRepository.save(any())).willReturn(메뉴_그룹);

        // when
        MenuGroup menuGroup = menuGroupService.create(메뉴_그룹_요청);

        // then
        assertAll(
                () -> assertThat(menuGroup.getId()).isInstanceOf(UUID.class),
                () -> assertThat(menuGroup.getName()).isEqualTo(name)
        );
    }

    @DisplayName("메뉴 그룹 이름이 null이거나 빈 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @NullAndEmptySource
    public void createWithName(String name) {
        // given
        MenuGroup 빈_메뉴_그룹_요청 = 메뉴_그룹_요청(name);

        // when, then
        assertThatThrownBy(() -> menuGroupService.create(빈_메뉴_그룹_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록된 메뉴 그룹을 모두 조회한다.")
    @Test
    public void findAll() {
        // given
        MenuGroup 한마리_메뉴_그룹 = 메뉴_그룹("한마리메뉴");
        MenuGroup 두마리_메뉴_그룹 = 메뉴_그룹("두마리메뉴");
        given(menuGroupRepository.findAll()).willReturn(Arrays.asList(
                한마리_메뉴_그룹,
                두마리_메뉴_그룹));

        // when
        List<MenuGroup> menuGroups = menuGroupService.findAll();

        // then
        assertAll(
                () -> assertThat(menuGroups.size()).isEqualTo(2),
                () -> assertThat(menuGroups).containsExactly(한마리_메뉴_그룹, 두마리_메뉴_그룹)
        );
    }
}