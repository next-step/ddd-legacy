package kitchenpos.menu_group.application;

import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kitchenpos.menu_group.fixture.MenuGroupFixture.createMenuGroup;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MenuGroup 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    MenuGroupRepository menuGroupRepository;

    @DisplayName("메뉴 그룹 이름이 null이거나 빈 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @NullAndEmptySource
    public void createWithName(String name) {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuGroup 두마리메뉴 = createMenuGroup(name);

        // when, then
        assertThatThrownBy(() -> menuGroupService.create(두마리메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }
}