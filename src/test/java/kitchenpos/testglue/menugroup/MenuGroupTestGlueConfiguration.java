package kitchenpos.testglue.menugroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import kitchenpos.application.MenuGroupService;
import kitchenpos.application.fixture.MenuGroupMother;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.util.testglue.TestGlueConfiguration;
import kitchenpos.util.testglue.TestGlueOperation;
import kitchenpos.util.testglue.TestGlueSupport;
import kitchenpos.util.testglue.test.TestGlueResponse;

@TestGlueConfiguration
public class MenuGroupTestGlueConfiguration extends TestGlueSupport {

	private final MenuGroupService menuGroupService;
	private final MenuGroupRepository menuGroupRepository;

	public MenuGroupTestGlueConfiguration(
		MenuGroupService menuGroupService,
		MenuGroupRepository menuGroupRepository
	) {
		this.menuGroupService = menuGroupService;
		this.menuGroupRepository = menuGroupRepository;
	}

	@TestGlueOperation("{} 메뉴 그룹을 생성하고")
	public void create_menu_group(String name) {
		MenuGroup menuGroup = MenuGroupMother.findByName(name);
		MenuGroup savedMenuGroup = menuGroupService.create(menuGroup);

		put(name, savedMenuGroup);
	}

	@TestGlueOperation("{} 메뉴그룹 데이터를 만들고")
	public void create_data(String name) {
		MenuGroup menuGroup = MenuGroupMother.findByName(name);

		put(name, menuGroup);
	}

	@TestGlueOperation("{} 메뉴그룹 생성을 요청하면")
	public void create_request(String name) {

		MenuGroup menuGroup = getAsType(name, MenuGroup.class);

		TestGlueResponse<MenuGroup> response = createResponse(() -> menuGroupService.create(menuGroup));
		put(name, response);
	}

	@TestGlueOperation("{} 메뉴그룹이 생성된다")
	public void create(String name) {
		TestGlueResponse<MenuGroup> response = getAsType(name, TestGlueResponse.class);

		MenuGroup menuGroup = response.getData();

		Optional<MenuGroup> savedMenuGroup = menuGroupRepository.findById(menuGroup.getId());
		assertThat(savedMenuGroup).isNotEmpty();
	}

	@TestGlueOperation("{} 메뉴그룹 생성에 실패한다")
	public void create_fail(String name) {
		TestGlueResponse<MenuGroup> response = getAsType(name, TestGlueResponse.class);

		assertThat(response.isOk()).isFalse();
	}
}
