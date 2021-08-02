package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class MenuGroupServiceTest extends MockTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    private MenuGroup menuGroup;
    private MenuGroup menuGroup2;

    @BeforeEach
    void setUp() {
        menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("메뉴그룹1");

        menuGroup2 = new MenuGroup();
        menuGroup2.setId(UUID.randomUUID());
        menuGroup2.setName(menuGroup.getName());
    }

    @DisplayName("메뉴 그룹을 추가할 수 있다")
    @Test
    void createOK() {
        //given
        given(menuGroupRepository.save(any())).willReturn(menuGroup);

        //when
        final MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

        //then
        assertThat(createdMenuGroup.getId()).isEqualTo(menuGroup.getId());
    }

    @DisplayName("메뉴 그룹 이름이 한글자 이상이 아니라면 예외를 던진다")
    @ParameterizedTest
    @NullAndEmptySource
    void createWithEmptyName(final String value) {
        //given
        menuGroup.setName(value);

        //when, then
        assertThatThrownBy(
            () -> menuGroupService.create(menuGroup)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 그룹 이름은 중복될 수 있다")
    @Test
    void createWithDuplicateName() {
        //given
        given(menuGroupRepository.save(any())).willReturn(menuGroup);
        given(menuGroupRepository.save(any())).willReturn(menuGroup2);

        //when
        final MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);
        final MenuGroup createdMenuGroup2 = menuGroupService.create(menuGroup2);

        //then
        assertThat(createdMenuGroup.getName()).isEqualTo(createdMenuGroup2.getName());
    }

    @DisplayName("메뉴 그룹 리스트를 조회할 수 있다")
    @Test
    void service() {
        //given
        given(menuGroupRepository.findAll()).willReturn(Arrays.asList(menuGroup, menuGroup2));

        //when
        final List<MenuGroup> menuGroups = menuGroupService.findAll();

        //then
        assertThat(menuGroup.getId()).isEqualTo(menuGroups.get(0)
            .getId());
    }

}
