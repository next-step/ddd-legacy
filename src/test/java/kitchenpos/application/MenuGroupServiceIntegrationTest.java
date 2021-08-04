package kitchenpos.application;


import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.MenuGroup;

class MenuGroupServiceIntegrationTest extends IntegrationTest {
	@Autowired
	private MenuGroupService menuGroupService;

	@Test
	void 메뉴_그룹_생성() {
		// given
		MenuGroup given = new MenuGroup();
		given.setName("추천메뉴");

		// when
		MenuGroup actual = menuGroupService.create(given);

		// then
		assertThat(actual.getId()).isNotNull();
		assertThat(actual.getName()).isEqualTo("추천메뉴");
	}

	@Test
	void 메뉴_그룹_생성_실패() {
		// given
		MenuGroup given = new MenuGroup();
		given.setName("");

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuGroupService.create(given);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}
}