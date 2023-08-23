package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @InjectMocks
    private MenuGroupService menuGroupService;
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @Test
    @DisplayName("메뉴 그룹을 생성한다.")
    void createTest() {
        // given
        MenuGroup request = new MenuGroup();
        request.setName("테스트 메뉴 그룹");

        // when
        menuGroupService.create(request);

        // then
        then(menuGroupRepository).should(times(1)).save(any(MenuGroup.class));
    }

    @Test
    @DisplayName("메뉴 그룹의 이름은 비울 수 없다.")
    void nameTest() {
        // given
        MenuGroup request = new MenuGroup();

        // when & then
        Assertions.assertThatThrownBy(() -> menuGroupService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }
}