package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static kitchenpos.stub.MenuGroupStub.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @DisplayName("새 메뉴그룹을 등록할 수 있다.")
    @Test
    void createNewMenuGroup() {
        //given
        MenuGroup newMenuGroup = generateFirstTestMenuGroup();
        when(menuGroupRepository.save(any())).thenReturn(newMenuGroup);

        //when
        MenuGroup result = menuGroupService.create(newMenuGroup);

        //then
        assertThat(result).isEqualTo(newMenuGroup);
    }

    @DisplayName("메뉴그룹 이름은 빈 값일 수 없다.")
    @Test
    void notAllowEmptyName() {
        //given
        MenuGroup newMenuGroup = generateEmptyNameMenuGroup();

        //when
        assertThatThrownBy(() -> menuGroupService.create(newMenuGroup)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴그룹 전체를 조회할 수 있다.")
    @Test
    void canFindAllMenuGroups() {
        //given
        List<MenuGroup> allMenuGroups = new ArrayList<>();
        allMenuGroups.add(generateFirstTestMenuGroup());
        allMenuGroups.add(generateSecondTestMenuGroup());
        when(menuGroupRepository.findAll()).thenReturn(allMenuGroups);

        //when
        List<MenuGroup> result = menuGroupService.findAll();

        //then
        assertThat(result).containsExactlyElementsOf(allMenuGroups);
    }

}