package kitchenpos.application;

import static kitchenpos.application.MenuGroupServiceFixture.menuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

class MenuGroupServiceTest {

    private MenuGroupRepository menuGroupRepository;

    private MenuGroupService menuGroupService;

    @BeforeEach
    void before() {
        menuGroupRepository = new FakeMenuGroupRepository();
        menuGroupService = new MenuGroupService(menuGroupRepository);
    }

    @DisplayName("메뉴 그룹을 등록할 수 있다.")
    @Test
    void create() {

        //given
        MenuGroup menuGroup = menuGroup();

        //when
        MenuGroup result = menuGroupService.create(menuGroup);

        //then
        assertThat(result.getName()).isEqualTo(menuGroup.getName());
    }

    @DisplayName("메뉴 그룹 이름은 비어 있을 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void can_not_be_empty_menuGroupName(String groupMenuName) {

        //given
        MenuGroup menuGroup = menuGroup();
        menuGroup.setName(groupMenuName);

        //when

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> menuGroupService.create(menuGroup));
    }

    @DisplayName("메뉴 그룹 목록을 조회할 수 있다. ")
    @Test
    void findAll() {

        //given
        menuGroupRepository.save(menuGroup());

        //when
        List<MenuGroup> result = menuGroupService.findAll();

        //then
        assertThat(result).hasSize(1);
    }
}
