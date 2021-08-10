package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kitchenpos.menu_group.step.MenuGroupStep.createMenuGroup;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MenuGroup 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    MenuGroupRepository menuGroupRepository;

    @DisplayName("메뉴 그룹 이름은 한글자 이상이다")
    @Test
    public void createWithName() {
        // given
        MenuGroupService menuGroupService = new MenuGroupService(menuGroupRepository);
        MenuGroup 두마리메뉴 = createMenuGroup(null);

        // when, then
        assertThatThrownBy(() -> menuGroupService.create(두마리메뉴))
                .isInstanceOf(IllegalArgumentException.class);
    }
}