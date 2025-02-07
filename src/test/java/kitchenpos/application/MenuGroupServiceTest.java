package kitchenpos.application;

import kitchenpos.application.fixture.MenuGroupFixture;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository repository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @DisplayName("메뉴 그룹을 생성할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"메인 메뉴"})
    void createMenuGroup(String name) {

        //given
        MenuGroup menuGroup = MenuGroupFixture.setMenuGroup(name);
        given(repository.save(any())).willReturn(menuGroup);

        //when
        MenuGroup resultMenuGroup = menuGroupService.create(menuGroup);

        //then
        assertAll(
                () -> assertThat(resultMenuGroup.getId()).isInstanceOf(UUID.class),
                () -> assertThat(resultMenuGroup.getName()).isEqualTo(name)
        );
    }

    @Test
    @DisplayName("메뉴 그룹을 조회할 수 있다.")
    void selectAllMenuGroup() {
        //given
        MenuGroup menuGroup1 = MenuGroupFixture.setMenuGroup("메인메뉴1");
        MenuGroup menuGroup2 = MenuGroupFixture.setMenuGroup("메인메뉴2");

        given(repository.findAll()).willReturn(List.of(menuGroup1, menuGroup2));

        //when
        List<MenuGroup> menuGroupList = menuGroupService.findAll();

        //then
        assertAll(
                () -> assertThat(menuGroupList.size()).isEqualTo(2),
                () -> assertThat(menuGroupList).containsExactly(menuGroup1, menuGroup2)
        );

    }

    @DisplayName("메뉴그룹명은 필수로 입력되어야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void nameCannotBeNullOrBlink(String name) {
        //given
        MenuGroup menuGroup = MenuGroupFixture.setMenuGroup(name);
        //when
        //then
        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
            .isInstanceOf(IllegalArgumentException.class);

    }
}
