package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static kitchenpos.fixture.MenuGroupFixtures.createMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    void 메뉴그룹은_등록할_수_있다() {
        //given
        MenuGroup menuGroup = createMenuGroup("메뉴그룹1");
        given(menuGroupRepository.save(any()))
                .willReturn(menuGroup);
        
        //when
        MenuGroup result = menuGroupService.create(menuGroup);

        //then
        assertThat(result.getName()).isEqualTo(menuGroup.getName());
        assertThat(result.getId()).isEqualTo(menuGroup.getId());
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    void 메뉴그룹의_이름은_비어있을_수_없다(String name) {
        //given
        MenuGroup menuGroup = createMenuGroup(name);

        // when, then
        assertThatThrownBy(() -> menuGroupService.create(menuGroup))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 모든_메뉴그룹을_조회할_수_있다() {
        //given
        MenuGroup menuGroup1 = createMenuGroup("메뉴그룹1");
        MenuGroup menuGroup2 = createMenuGroup("메뉴그룹2");
        List<MenuGroup> menuGroups = List.of(menuGroup1, menuGroup2);

        given(menuGroupRepository.findAll())
                .willReturn(menuGroups);

        //when
        List<MenuGroup> result = menuGroupService.findAll();

        //then
        assertThat(result.size()).isEqualTo(menuGroups.size());
    }
}