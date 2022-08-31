package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import kitchenpos.common.MockitoUnitTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.stub.MenuGroupStub;

class MenuGroupServiceTest extends MockitoUnitTest {

	@Mock
	private MenuGroupRepository menuGroupRepository;

	@InjectMocks
	private MenuGroupService menuGroupService;

	@DisplayName("메뉴그룹 전체를 조회할 수 있다.")
	@Test
	void findAll() {
		//given
		List<MenuGroup> allMenuGroups = List.of(MenuGroupStub.createDefault(), MenuGroupStub.createDefault());

		when(menuGroupRepository.findAll())
			.thenReturn(allMenuGroups);

		//when
		List<MenuGroup> result = menuGroupService.findAll();

		//then
		assertAll(
			() -> assertThat(result).isNotEmpty(),
			() -> assertThat(result).containsExactlyElementsOf(allMenuGroups)
		);
	}

	@Nested
	@DisplayName("메뉴그룹 등록 시")
	class CreateTest {

		@DisplayName("새 메뉴그룹을 등록할 수 있다.")
		@Test
		void create() {
			//given
			MenuGroup menuGroup = MenuGroupStub.createDefault();

			when(menuGroupRepository.save(any()))
				.thenReturn(menuGroup);

			//when
			MenuGroup result = menuGroupService.create(menuGroup);

			//then
			assertThat(result)
				.isEqualTo(menuGroup);
		}

		@DisplayName("이름은 빈 값일 수 없다.")
		@ParameterizedTest
		@NullAndEmptySource
		void createFailByEmptyName(String name) {
			//when
			MenuGroup menuGroup = MenuGroupStub.createCustom(name);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> menuGroupService.create(menuGroup));
		}
	}
}
