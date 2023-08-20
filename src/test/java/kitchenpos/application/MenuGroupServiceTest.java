package kitchenpos.application;

import kitchenpos.ApplicationTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("메뉴그룹")
class MenuGroupServiceTest extends ApplicationTest {

    @Mock
    private PurgomalumClient purgomalumClient;
    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @DisplayName("[성공] 메뉴그룹을 생성한다.")
    @Test
    void createTest1() {
        //given
        MenuGroup menuGroup = MenuGroupFixture.create("메뉴그룹");
        when(menuGroupRepository.save(any())).thenReturn(menuGroup);
        //when
        MenuGroup created = menuGroupService.create(menuGroup);
        //then
        assertThat(created).isEqualTo(created);
    }

    @DisplayName("[예외] 메뉴그룹 이름은 공백일 수 없다.")
    @ParameterizedTest
    @NullSource
    void nameTest1(String name) {
        MenuGroup menuGroup = MenuGroupFixture.create(name);
        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[성공] 메뉴그룹 전체 목록을 조회한다.")
    @Test
    void findAllTest1() {
        //given
        MenuGroup menuGroup1 = MenuGroupFixture.createDefault();
        MenuGroup menuGroup2 = MenuGroupFixture.createDefault();
        MenuGroup menuGroup3 = MenuGroupFixture.createDefault();
        when(menuGroupRepository.findAll()).thenReturn(List.of(menuGroup1, menuGroup2, menuGroup3));
        //when
        List<MenuGroup> all = menuGroupService.findAll();
        //then
        assertThat(all).hasSize(3)
                .contains(menuGroup1, menuGroup2, menuGroup3);
    }

}
