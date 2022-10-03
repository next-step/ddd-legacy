package kitchenpos.application;

import kitchenpos.configuration.TestIsolationSupport;
import kitchenpos.util.testglue.EnableTestGlue;
import kitchenpos.util.testglue.TestGlue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@EnableTestGlue
@SpringBootTest
class OrderTableServiceTest extends TestIsolationSupport {

	@Autowired
	private TestGlue testGlue;

	@DisplayName("주문 테이블을 생성한다.")
	@Test
	void create() {
		testGlue.builder()
			.given("'테이블1' 주문 테이블을 생성하면")
			.then("'테이블1' 주문 테이블이 생성된다")
			.assertStart();
	}

	@DisplayName("주문 테이블 이름은 비어있을 수 없다.")
	@Test
	void createWithEmptyName() {
		testGlue.builder()
			.given("'' 주문 테이블을 생성하면")
			.then("주문 테이블 생성에 실패한다")
			.assertStart();
	}

	@DisplayName("주문 테이블에 손님이 앉으면 주문 테이블이 occupied상태가 된다.")
	@Test
	void sit() {
		testGlue.builder()
			.given("'테이블1' 주문 테이블을 생성하고")
			.when("'테이블1' 주문 테이블에 손님이 앉으면")
			.then("'테이블1' 주문 테이블이 occupied상태가 된다.")
			.assertStart();
	}

	@DisplayName("주문 테이블 초기화를 요청하면, 주문 테이블은 초기화 된다.")
	@Test
	void clear() {
		홀_주문을_생성하고()
			.given("'홀주문' 주문을 수락하고")
			.given("'홀주문' 주문을 서빙하고")
			.given("'홀주문' 주문을 종료하고")
			.when("'테이블1' 주문 테이블을 정리하면")
			.then("주문 테이블은 초기화 된다")
			.assertStart();
	}

	@DisplayName("주문 테이블에 연관된 주문이 COMPLETED 상태가 아니면, 초기화 할 수 없다.")
	@Test
	void clea_fail() {
		홀_주문을_생성하고()
			.given("'홀주문' 주문을 수락하고")
			.given("'홀주문' 주문을 서빙하고")
			.when("'테이블1' 주문 테이블을 정리하면")
			.then("주문 테이블 초기화에 실패한다")
			.assertStart();
	}

	private TestGlue.Builder 추천_메뉴를_생성하고() {
		return testGlue.builder()
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '추천메뉴' 메뉴 데이터를 만들고")
			.given("'추천메뉴' 메뉴를 생성하고");
	}

	private TestGlue.Builder 홀_주문을_생성하고() {
		return 추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴 '4' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.given("'테이블1' 주문 테이블을 생성하고")
			.given("'테이블1' 주문 테이블에 손님이 앉고")
			.when("'테이블1' 주문 테이블과 '추천메뉴주문정보' 주문메뉴 정보로 '홀주문' 주문을 생성하고");
	}

	@DisplayName("주문 테이블 손님의 수를 변경할 수 있다.")
	@Test
	void changeNumberOfGuests() {
		testGlue.builder()
			.given("'테이블1' 주문 테이블을 생성하고")
			.given("'테이블1' 주문 테이블에 손님이 앉고")
			.when("'테이블1' 주문 테이블 손님의 수를 '4' 로 변경하면")
			.then("'테이블1' 의 손님의 수가 '4'로 변경된다")
			.assertStart();
	}

	@DisplayName("occupied 상태가 아니라면 주문 테이블 손님의 수를 변경할 수 있다.")
	@Test
	void changeNumberOfGuests_notOccupied() {
		testGlue.builder()
			.given("'테이블1' 주문 테이블을 생성하고")
			.when("'테이블1' 주문 테이블 손님의 수를 '4' 로 변경하면")
			.then("주문 테이블 변경에 실패한다")
			.assertStart();
	}
}
