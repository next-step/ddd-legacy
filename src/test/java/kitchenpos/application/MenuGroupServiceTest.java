package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    private static final String NAME = "NEW MENU GROUP";
    private MenuGroup expectedMenuGroup;

    @BeforeEach
    void setUp() {
        expectedMenuGroup = new MenuGroup();
        expectedMenuGroup.setName(NAME);
    }


    @DisplayName("새로운 메뉴 그룹을 추가할 수 있다.")
    @Test
    void create() {
        // given
        given(menuGroupRepository.save(any(MenuGroup.class)))
                .willReturn(expectedMenuGroup);

        // when
        final MenuGroup actual = menuGroupService.create(expectedMenuGroup);

        // then
        assertThat(actual.getName())
                .isEqualTo(NAME);
    }

    @DisplayName("이름이 없는 메뉴그룹은 추가할 수 없다.")
    @Test
    void create_emptyName() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuGroupService.create(new MenuGroup()));
    }

    @DisplayName("메뉴 그룹의 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        given(menuGroupRepository.save(any(MenuGroup.class)))
                .willReturn(expectedMenuGroup);

        // when
        final MenuGroup actual = menuGroupService.create(expectedMenuGroup);

        // then
        assertThat(actual.getName())
                .isEqualTo(NAME);
    }
}
