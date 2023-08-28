package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static kitchenpos.fixture.MenuGroupFixture.MENU_GROUP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    @DisplayName("메뉴 그룹을 생성한다.")
    void createTest() {
        // given
        MenuGroup expected = MENU_GROUP();
        given(menuGroupRepository.save(any(MenuGroup.class))).willReturn(expected);

        // when
        MenuGroup actual = menuGroupService.create(expected);

        // then
        then(menuGroupRepository).should(times(1)).save(any(MenuGroup.class));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("메뉴 그룹명이 있어야 한다.")
    void nameTest() {
        // given
        MenuGroup expected = new MenuGroup();

        // when & then
        assertThatThrownBy(() -> menuGroupService.create(expected))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("모든 메뉴 그룹 정보를 가져온다")
    void findAllTest() {
        // given
        MenuGroup expected = MENU_GROUP();
        given(menuGroupRepository.findAll()).willReturn(List.of(expected));

        // when
        List<MenuGroup> actual = menuGroupService.findAll();

        // then
        then(menuGroupRepository).should(times(1)).findAll();
        assertThat(actual).hasSize(1);
        assertThat(actual).containsExactly(expected);
    }
}
