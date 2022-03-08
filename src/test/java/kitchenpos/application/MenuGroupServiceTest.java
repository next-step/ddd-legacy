package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

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
	//  - [ ] 도메인 정보
//    - [ ] 메뉴를 분류하는 이름을 가집니다.
//		- [ ] 메뉴 그룹 안에는 메뉴 그룹에 속한 메뉴 정보들이 나타납니다.
//  - [ ] 서비스
//    - [ ] 가게 점주는 메뉴 그룹을 생성할 수 있습니다.
//      - [ ] 메뉴를 분류할 수 있도록 이름을 지정해 생성합니다.
//      - [ ] 메뉴를 분류하는 그룹에는 이름이 꼭 필요합니다.
//		- [ ] 가게 점주와 가게 손님은 모든 메뉴 그룹을 가져올 수 있습니다.


	@Test
	@DisplayName("도메인 정보")
	void domainInformation() {

	}

	@Test
	@DisplayName("가게 점주는 메뉴 그룹을 생성할 수 있습니다.")
	void createMenuGroup() {
		MenuGroup menuGroup = getMenuGroup(FIRST_MENU_GROUP);
		when(menuGroupRepository.save(any())).thenReturn(menuGroup);

		MenuGroup createMenuGroup = menuGroupService.create(menuGroup);
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

		assertThat(findMenuGroupList.size()).isEqualTo(menuGroupList.size());
		assertThat(menuGroupList).isSameAs(findMenuGroupList);
	}

	private MenuGroup getMenuGroup(String name) {
		MenuGroup menuGroup = new MenuGroup();
		menuGroup.setName(name);
		return menuGroup;
	}
}