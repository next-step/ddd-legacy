package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.fixture.MenuGroupFixture;

class MenuGroupServiceIntegrationTest extends IntegrationTest {
	@Autowired
	private MenuGroupService menuGroupService;
	@Autowired
	private MenuGroupRepository menuGroupRepository;

	@DisplayName("메뉴 그룹 생성")
	@Test
	void 메뉴_그룹_생성() {
		// given
		MenuGroup givenRequest = new MenuGroup();
		givenRequest.setName("추천메뉴");

		// when
		MenuGroup actualMenuGroup = menuGroupService.create(givenRequest);

		// then
		assertThat(actualMenuGroup.getId()).isNotNull();
		assertThat(actualMenuGroup.getName()).isEqualTo("추천메뉴");
	}

	@DisplayName("메뉴 그룹 생성 실패 : 이름 빈값")
	@ParameterizedTest
	@NullAndEmptySource
	void 메뉴_그룹_생성_실패_1(String name) {
		// given
		MenuGroup givenRequest = new MenuGroup();
		givenRequest.setName(name);

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuGroupService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}

	@DisplayName("전체 주문 조회")
	@Test
	void 전체_주문_조회() {
		// given
		menuGroupRepository.save(MenuGroupFixture.menuGroup());

		// when
		List<MenuGroup> actual = menuGroupService.findAll();

		// then
		assertThat(actual).isNotEmpty();
	}
}