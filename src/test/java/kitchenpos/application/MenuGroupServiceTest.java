package kitchenpos.application;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

	@Mock
	private MenuGroupRepository menuGroupRepository;

	@InjectMocks
	private MenuGroupService menuGroupService;

	@DisplayName("메뉴 그룹 전체를 조회할 수 있다")
	@Test
	void find_all_menu_group() {
		// given
		MenuGroup menuGroup1 = new MenuGroup();
		menuGroup1.setName("메뉴 그룹 1");
		MenuGroup menuGroup2 = new MenuGroup();
		menuGroup2.setName("메뉴 그룹 2");
		when(menuGroupRepository.findAll()).thenReturn(Arrays.asList(menuGroup1, menuGroup2));

		// when
		List<MenuGroup> menuGroups = menuGroupService.findAll();

		// then
		Assertions.assertThat(menuGroups.size()).isEqualTo(2);
		Assertions.assertThat(menuGroups.get(0).getName()).isEqualTo("메뉴 그룹 1");
		Assertions.assertThat(menuGroups.get(1).getName()).isEqualTo("메뉴 그룹 2");
	}

	@DisplayName("메뉴그룹을 만들 때 메뉴 그룹의 이름이 비어 있으면 에러가 발생한다")
	@Test
	void menu_group_name_is_empty_then_error_occurs() {
		// given
		MenuGroup menuGroupRequestWithNameNull = new MenuGroup();
		MenuGroup menuGroupRequestWithEmptyName = new MenuGroup();
		// when & then
		Assertions.assertThatThrownBy(() -> menuGroupService.create(menuGroupRequestWithNameNull)).isInstanceOf(IllegalArgumentException.class);
		Assertions.assertThatThrownBy(() -> menuGroupService.create(menuGroupRequestWithEmptyName)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴 그룹을 만들 수 있다")
	@Test
	void create_menu() {
		// given
		MenuGroup menuGroupRequest = new MenuGroup();
		menuGroupRequest.setName("메뉴 그룹");
		MenuGroup menuGroupResult = new MenuGroup();
		menuGroupResult.setName("메뉴 그룹");

		when(menuGroupRepository.save(any())).thenReturn(menuGroupResult);
		// when
		MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

		// then
		Assertions.assertThat(menuGroup.getName()).isEqualTo("메뉴 그룹");
	}
}
