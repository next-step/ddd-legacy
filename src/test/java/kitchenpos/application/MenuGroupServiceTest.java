package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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

	private MenuGroup menuGroup;

	@BeforeEach
	void setUp() {
		menuGroup = new MenuGroup();
		menuGroup.setId(UUID.randomUUID());
		menuGroup.setName("점심 특선");
	}

	@ParameterizedTest
	@DisplayName("메뉴 그룹의 이름이 null이거나 비어있으면 IllegalArgumentException이 발생한다")
	@NullAndEmptySource
	void createMenuGroupWithEmptyOrNullName(String name) {
		// given
		MenuGroup menuGroup = new MenuGroup();
		menuGroup.setName(name);

		// then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() ->
				// when
				menuGroupService.create(menuGroup)
			);
	}

	@Test
	@DisplayName("메뉴 그룹의 이름이 비어있지 않으면 정상적으로 저장된다")
	void createMenuGroupWithValidName() {
		// given
		when(menuGroupRepository.save(any(MenuGroup.class))).thenReturn(menuGroup);

		// when
		MenuGroup created = menuGroupService.create(menuGroup);

		// then
		assertThat(created).isNotNull();
		assertThat(created.getName()).isEqualTo("점심 특선");
		assertThat(created.getId()).isNotNull();
	}

	@Test
	@DisplayName("모든 메뉴 그룹을 정상적으로 조회하면 목록이 비어있지 않다")
	void findAllMenuGroups() {
		// given
		when(menuGroupRepository.findAll()).thenReturn(Collections.singletonList(menuGroup));

		// when
		List<MenuGroup> foundMenuGroups = menuGroupService.findAll();

		// then
		assertThat(foundMenuGroups).isNotEmpty();
		assertThat(foundMenuGroups.get(0).getName()).isEqualTo("점심 특선");
	}
}