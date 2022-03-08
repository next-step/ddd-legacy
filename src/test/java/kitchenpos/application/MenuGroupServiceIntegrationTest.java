package kitchenpos.application;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuGroupServiceIntegrationTest extends IntegrationTest {
	private static final String NAME = "추천메뉴";

	@Autowired
	private MenuGroupService menuGroupService;
	@Autowired
	private MenuGroupRepository menuGroupRepository;

	@DisplayName("메뉴 그룹 조회")
	@Test
	void readAllGroupMenu() {
		//given
		MenuGroup given = menuGroupRepository.save(MenuGroupFixture.MENU_GROUP());

		//when
		List<MenuGroup> menuGroups = menuGroupService.findAll();

		//then
		List<UUID> actualIds = menuGroups.stream().map(MenuGroup::getId).collect(Collectors.toList());

		assertAll(
				() -> assertThat(menuGroups).isNotEmpty(),
				() -> assertThat(actualIds).contains(given.getId())
		);
	}

	@DisplayName("메뉴 그룹 생성")
	@Test
	void createMenuGroup() {
		// given
		MenuGroup menuGroup = new MenuGroup();
		menuGroup.setName(NAME);

		// when
		MenuGroup createdMenuGroup = menuGroupService.create(menuGroup);

		// then
		assertThat(createdMenuGroup.getId()).isNotNull();
		assertThat(createdMenuGroup.getName()).isEqualTo(NAME);
	}

	@DisplayName("이름이 null이거나 empty일때 메뉴 그룹 생성 실패한다")
	@ParameterizedTest
	@NullAndEmptySource
	void failCreatingMenuGroupWhenNameIsNullOrEmpty(String name) {
		// given
		MenuGroup menuGroup = new MenuGroup();
		menuGroup.setName(name);

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuGroupService.create(menuGroup);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

}
