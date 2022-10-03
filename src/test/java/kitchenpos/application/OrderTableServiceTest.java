package kitchenpos.application;

import kitchenpos.util.testglue.EnableTestGlue;
import kitchenpos.util.testglue.TestGlue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@EnableTestGlue
@SpringBootTest
class OrderTableServiceTest {

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

	@Test
	void clear() {
		//todo 주문 로직이 구현되고 할 것.
	}

	@DisplayName("주문 테이블 손님의 수를 변경할 수 있다.")
	@Test
	void changeNumberOfGuests() {
		testGlue.builder()
			.given("'테이블1' 주문 테이블을 생성하고")
			.given("'테이블1' 주문 테이블에 손님이 앉고")
			.when("손님의 수를 '4' 로 변경하면")
			.then("'테이블1' 의 손님의 수가 '4'로 변경된다")
			.assertStart();
	}

	@DisplayName("occupied 상태가 아니라면 주문 테이블 손님의 수를 변경할 수 있다.")
	@Test
	void changeNumberOfGuests_notOccupied() {
		testGlue.builder()
			.given("'테이블1' 주문 테이블을 생성하고")
			.when("손님의 수를 '4' 로 변경하면")
			.then("주문 테이블 변경에 실패한다")
			.assertStart();
	}
}
