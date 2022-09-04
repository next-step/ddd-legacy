package kitchenpos.testglue.menugroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import kitchenpos.application.MenuGroupService;
import kitchenpos.application.ProductService;
import kitchenpos.application.fixture.MenuGroupMother;
import kitchenpos.application.fixture.ProductMother;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.util.testglue.TestGlueConfiguration;
import kitchenpos.util.testglue.TestGlueOperation;
import kitchenpos.util.testglue.TestGlueSupport;

@TestGlueConfiguration
public class menuGroupTestGlueConfiguration extends TestGlueSupport {

	private final MenuGroupService menuGroupService;
	private final MenuGroupRepository menuGroupRepository;

	public menuGroupTestGlueConfiguration(
		MenuGroupService menuGroupService,
		MenuGroupRepository menuGroupRepository
	) {
		this.menuGroupService = menuGroupService;
		this.menuGroupRepository = menuGroupRepository;
	}

	@TestGlueOperation("{} 메뉴그룹 데이터를 만들고")
	public void create_data(String name) {
		MenuGroup menuGroup = MenuGroupMother.findByName(name);

		put(name, menuGroup);
	}

	@TestGlueOperation("{} 메뉴그룹 생성을 요청하면")
	public void create_request(String name) {
		try {
			MenuGroup menuGroup = getAsType(name, MenuGroup.class);

			MenuGroup savedMenuGroup = menuGroupService.create(menuGroup);
			put(name, savedMenuGroup);
		} catch (Exception ignore) {
		}
	}

	@TestGlueOperation("{} 메뉴그룹이 생성된다")
	public void create(String name) {
		MenuGroup menuGroup = getAsType(name, MenuGroup.class);

		Optional<MenuGroup> savedMenuGroup = menuGroupRepository.findById(menuGroup.getId());
		assertThat(savedMenuGroup).isNotEmpty();
	}

	@TestGlueOperation("{} 메뉴그룹 생성에 실패한다")
	public void create_fail(String name) {
		MenuGroup menuGroup = getAsType(name, MenuGroup.class);

		Optional<MenuGroup> savedMenuGroup = menuGroupRepository.findById(menuGroup.getId());
		assertThat(savedMenuGroup).isEmpty();
	}
}
