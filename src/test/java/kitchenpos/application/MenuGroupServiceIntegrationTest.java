package kitchenpos.application;


import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.MenuGroup;

class MenuGroupServiceIntegrationTest extends IntegrationTest {
	@Autowired
	private MenuGroupService menuGroupService;

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
	@Test
	void 메뉴_그룹_생성_실패_1() {
		// given
		MenuGroup givenRequest = new MenuGroup();
		givenRequest.setName("");

		// when
		ThrowableAssert.ThrowingCallable throwingCallable = () -> menuGroupService.create(givenRequest);

		// then
		Assertions.assertThatIllegalArgumentException().isThrownBy(throwingCallable);
	}
}