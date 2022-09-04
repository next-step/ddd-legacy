package kitchenpos.application;



import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("메뉴 그룹 테스트")
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    private MenuGroup menuGroup1;
    private MenuGroup menuGroup2;

    @BeforeEach
    void setUp() {
        menuGroup1 = new MenuGroup();
        menuGroup1.setName("그룹1");
        menuGroup1.setId(UUID.randomUUID());

        menuGroup2 = new MenuGroup();
        menuGroup2.setName("그룹2");
        menuGroup2.setId(UUID.randomUUID());
    }

    @DisplayName("메뉴 그룹을 생성 할때 이름은 필수 이다.")
    @ParameterizedTest
    @NullAndEmptySource
    void name_is_Not_Empty(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);

        assertThatIllegalArgumentException().isThrownBy(() ->
            menuGroupService.create(menuGroup)
        );
    }

    @DisplayName("메뉴 그룹을 생성 한다.")
    @Test
    void create() {
        //given
        MenuGroup resultMenu = menuGroup1;

        given(menuGroupRepository.save(any())).willReturn(menuGroup1);
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("그룹1");

        //when
        final MenuGroup createMenuGroup = menuGroupService.create(menuGroup);

        //then
        assertAll(
                () -> assertThat(createMenuGroup.getName()).isEqualTo(resultMenu.getName()),
                () -> assertThat(createMenuGroup.getId()).isEqualTo(resultMenu.getId())
        );
    }

    @DisplayName("메뉴 그룹이 조회 된다.")
    @Test
    void findMenuGroup() {
        //given
        List<MenuGroup> resultMenuGroup = List.of(menuGroup1, menuGroup2);
        given(menuGroupRepository.findAll()).willReturn(resultMenuGroup);

        //when
        final List<MenuGroup> menuGroups = menuGroupService.findAll();

        assertAll(
                ()-> assertThat(menuGroups.size()).isEqualTo(2),
                ()-> assertThat(menuGroups).contains(menuGroup1, menuGroup2)
        );
    }

}
