package kitchenpos.application;

import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP1;
import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP2_SAME_NAME;
import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP_WITH_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class MenuGroupServiceTest extends MockTest {

    private static final int ZERO = 0;

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @DisplayName("create - 메뉴 그룹을 추가할 수 있다")
    @Test
    void createOK() {
        //given
        given(menuGroupRepository.save(any())).willReturn(MENU_GROUP1());

        //when
        final MenuGroup sut = menuGroupService.create(MENU_GROUP1());

        //then
        assertAll(
            () -> assertThat(sut.getId()).isEqualTo(MENU_GROUP1().getId()),
            () -> assertThat(sut.getName()).isEqualTo(MENU_GROUP1().getName())
        );
    }

    @DisplayName("create - 메뉴 그룹 이름이 한글자 이상이 아니라면 예외를 던진다")
    @ParameterizedTest
    @NullAndEmptySource
    void createWithEmptyName(final String value) {
        //given
        final MenuGroup menuGroup = MENU_GROUP_WITH_NAME(value);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuGroupService.create(menuGroup));
    }

    @DisplayName("create - 메뉴 그룹 이름은 중복될 수 있다")
    @Test
    void createWithDuplicateName() {
        //given
        given(menuGroupRepository.save(any())).willReturn(MENU_GROUP1());
        given(menuGroupRepository.save(any())).willReturn(MENU_GROUP2_SAME_NAME());

        //when
        final MenuGroup sut1 = menuGroupService.create(MENU_GROUP1());
        final MenuGroup sut2 = menuGroupService.create(MENU_GROUP2_SAME_NAME());

        //then
        assertThat(sut1.getName()).isEqualTo(sut2.getName());
    }

    @DisplayName("findAll - 메뉴 그룹 리스트를 조회할 수 있다")
    @Test
    void findAll() {
        //given
        given(menuGroupRepository.findAll()).willReturn(Arrays.asList(MENU_GROUP1(), MENU_GROUP2_SAME_NAME()));

        //when
        final List<MenuGroup> menuGroups = menuGroupService.findAll();

        //then
        assertThat(MENU_GROUP1().getId())
            .isEqualTo(menuGroups.get(ZERO)
                .getId());
    }

}
