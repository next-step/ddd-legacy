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

import java.util.List;

import static kitchenpos.fixture.TestFixture.TEST_MENU_GROUP;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
        MenuGroup request = TEST_MENU_GROUP();
        given(menuGroupRepository.save(any(MenuGroup.class))).willReturn(request);
        // when
        MenuGroup actual = menuGroupService.create(request);

        // then
        then(menuGroupRepository).should(times(1)).save(any(MenuGroup.class));
        assertThat(actual).isEqualTo(request);
    }

    @Test
    @DisplayName("메뉴 그룹의 이름은 빈값일 수 없다")
    void nameTest() {
        // given
        MenuGroup request = new MenuGroup();

        // when & then
        assertThatThrownBy(() -> menuGroupService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("모든 메뉴 그룹 정보를 가져온다")
    void findAllTest() {
        // given
        MenuGroup menuGroup = TEST_MENU_GROUP();
        given(menuGroupRepository.findAll()).willReturn(List.of(menuGroup));

        // when
        List<MenuGroup> actual = menuGroupService.findAll();

        // then
        then(menuGroupRepository).should(times(1)).findAll();
        assertThat(actual).hasSize(1);
        assertThat(actual).containsExactly(menuGroup);
    }
}