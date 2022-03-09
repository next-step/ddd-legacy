package kitchenpos.application;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kitchenpos.testBuilders.MenuGroupBuilder.DEFAULT_MENU_GROUP_NAME;
import static kitchenpos.testBuilders.MenuGroupBuilder.aMenuGroup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MenuGroupServiceTest {

	@Mock
	MenuGroupRepository menuGroupRepository;

	@InjectMocks
	MenuGroupService menuGroupService;

	MenuGroup mockResultMenuGroup;

	@DisplayName("메뉴그룹을 생성하여 반환한다")
	@Test
	void create() {
		// given
		MenuGroup request = aMenuGroup().withName(DEFAULT_MENU_GROUP_NAME).build();

		given(menuGroupRepository.save(any(MenuGroup.class))).willReturn(mockResultMenuGroup);

		// when
		MenuGroup result = menuGroupService.create(request);

		// then
		assertThat(result).isEqualTo(mockResultMenuGroup);
	}

	@DisplayName("메뉴그룹 생성 시 메뉴그룹 이름이 비어있는 경우 예외가 발생한다")
	@ParameterizedTest(name = "메뉴그룹의 이름: {0}")
	@NullAndEmptySource
	void nameEmpty(String name) {
		// given
		MenuGroup request = aMenuGroup().withName(name).build();

		// when then
		assertThatThrownBy(() -> menuGroupService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}
}
