package kitchenpos.application;

import static org.junit.jupiter.api.Assertions.*;

import kitchenpos.configuration.TestIsolationSupport;
import kitchenpos.util.testglue.EnableTestGlue;
import kitchenpos.util.testglue.TestGlue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@EnableTestGlue
@SpringBootTest
class MenuGroupServiceTest extends TestIsolationSupport {

	@Autowired
	private TestGlue testGlue;

	@DisplayName("메뉴그룹을 생성한다.")
	@Test
	void create() {
		testGlue.builder()
			.given("'추천메뉴그룹' 메뉴그룹 데이터를 만들고")
			.when("'추천메뉴그룹' 메뉴그룹 생성을 요청하면")
			.then("'추천메뉴그룹' 메뉴그룹이 생성된다")
			.assertStart();
	}

	@DisplayName("메뉴그룹 이름은 비어있을 수 없다.")
	@Test
	void create_emptyName() {
		testGlue.builder()
			.given("'이름이 빈 메뉴그룹' 메뉴그룹 데이터를 만들고")
			.when("'이름이 빈 메뉴그룹' 메뉴그룹 생성을 요청하면")
			.then("'이름이 빈 메뉴그룹' 메뉴그룹 생성에 실패한다")
			.assertStart();
	}
}
