package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {
	public static final String FIRST_MENU_GROUP = "첫 번째 테스트 메뉴 그룹";
	public static final String SECOND_MENU_GROUP = "두 번째 테스트 메뉴 그룹";

	@Mock
	private MenuGroupRepository menuGroupRepository;

	@InjectMocks
	private MenuGroupService menuGroupService;

	private static Stream<String> menuNameIsEmptyAndNull() {
		return Stream.of(
			null,
			""
		);
	}

	@MethodSource("menuNameIsEmptyAndNull")
	@ParameterizedTest
	@DisplayName("메뉴를 분류하는 그룹에는 이름이 꼭 필요합니다.")
	void createMenuGroupButNameless(String name) {
		//given
		MenuGroup menuGroup = mock(MenuGroup.class);
		when(menuGroup.getName()).thenReturn(name);

		//when & then
		assertThatThrownBy(() -> menuGroupService.create(menuGroup))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("가게 점주는 메뉴 그룹을 생성할 수 있습니다.")
	void createMenuGroup() {
		MenuGroup menuGroup = getMenuGroup(FIRST_MENU_GROUP);
		when(menuGroupRepository.save(any())).thenReturn(menuGroup);

		MenuGroup createMenuGroup = menuGroupService.create(menuGroup);

		verify(menuGroupRepository).save(any(MenuGroup.class));
		assertThat(createMenuGroup).isEqualTo(menuGroup);
	}

	@Test
	@DisplayName("가게 점주와 가게 손님은 모든 메뉴 그룹을 가져올 수 있습니다.")
	void findMenuGroupAll() {
		MenuGroup firstMenuGroup = getMenuGroup(FIRST_MENU_GROUP);
		MenuGroup secondMenuGroup = getMenuGroup(SECOND_MENU_GROUP);

		List<MenuGroup> menuGroupList = new ArrayList<>();
		menuGroupList.add(firstMenuGroup);
		menuGroupList.add(secondMenuGroup);

		when(menuGroupRepository.findAll()).thenReturn(menuGroupList);

		List<MenuGroup> findMenuGroupList = menuGroupService.findAll();

		verify(menuGroupRepository).findAll();

		assertThat(findMenuGroupList.size()).isEqualTo(menuGroupList.size());
		assertThat(menuGroupList).isSameAs(findMenuGroupList);
	}

	private static MenuGroup getMenuGroup(String name) {
		MenuGroup menuGroup = new MenuGroup();
		menuGroup.setId(UUID.randomUUID());
		menuGroup.setName(name);
		return menuGroup;
	}
}