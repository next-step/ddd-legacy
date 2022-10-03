package kitchenpos.application;

import kitchenpos.configuration.TestIsolationSupport;
import kitchenpos.util.testglue.EnableTestGlue;
import kitchenpos.util.testglue.TestGlue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableTestGlue
class OrderServiceTest extends TestIsolationSupport {

	@Autowired
	private TestGlue testGlue;

	@DisplayName("주문을 생성한다.")
	@Test
	void create() {
		추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴 '4' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.when("'추천메뉴주문정보' 주문메뉴 정보로 '배달주문' 주문을 생성하면")
			.then("주문이 생성된다")
			.assertStart();;
	}

	@DisplayName("주문이 주문메뉴정보를 가지지 않으면 생성에 실패한다.")
	@Test
	void create_noOrderLineItem() {
		추천_메뉴를_생성하고()
			.when("'비어있는' 주문메뉴 정보로 '배달주문' 주문을 생성하면")
			.then("주문이 실패한다")
			.assertStart();
	}

	@DisplayName("주문메뉴정보가 존재하지 않는 메뉴를 가지면 생성에 실패한다.")
	@Test
	void create_orderLineItemValidation_noExistMenu() {
		추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴를 삭제하고")
			.given("'추천메뉴' 메뉴 '4' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.when("'추천메뉴주문정보' 주문메뉴 정보로 '배달주문' 주문을 생성하면")
			.then("주문이 실패한다")
			.assertStart();
	}

	@DisplayName("수량은 음수가 될 수 없다.")
	@Test
	void create_orderLineItemValidation_minusQuantityNotInHole() {
		추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴 '-1' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.when("'추천메뉴주문정보' 주문메뉴 정보로 '배달주문' 주문을 생성하면")
			.then("주문이 실패한다")
			.assertStart();
	}

	@DisplayName("홀 주문은 수량이 음수가 될 수 있다.")
	@Test
	void create_orderLineItemValidation_minusQuantityInHole() {
		추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴 '-1' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.given("'테이블1' 주문 테이블을 생성하고")
			.given("'테이블1' 주문 테이블에 손님이 앉고")
			.when("'테이블1' 주문 테이블과 '추천메뉴주문정보' 주문메뉴 정보로 '홀주문' 주문을 생성하면")
			.then("주문이 생성된다")
			.assertStart();
	}

	@DisplayName("모든 메뉴는 전시중이어야 한다")
	@Test
	void create_orderLineItemValidation_allMenuDisplayed() {
		추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴 '4' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.given("'추천메뉴' 메뉴를 미전시상태로 변경하고")
			.when("'추천메뉴주문정보' 주문메뉴 정보로 '배달주문' 주문을 생성하면")
			.then("주문이 실패한다")
			.assertStart();
	}

	@DisplayName("메뉴 가격과 주문메뉴정보에 명시된 가격은 동일해야 한다")
	@Test
	void create_orderLineItemValidation_menuAndOrderLineItemPriceMustEqual() {
		추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴 '4' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.given("'추천메뉴주문정보' 주문메뉴정보를 가격을 '999999'로 변경하고")
			.when("'추천메뉴주문정보' 주문메뉴 정보로 '배달주문' 주문을 생성하면")
			.then("주문이 실패한다")
			.assertStart();
	}

	@DisplayName("주문의 초기 상태는 WAITING이다.")
	@Test
	void create_initStatusIsWaiting() {
		추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴 '4' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.when("'추천메뉴주문정보' 주문메뉴 정보로 '배달주문' 주문을 생성하면")
			.then("주문의 상태는 'WAITING' 이다")
			.assertStart();
	}

	@DisplayName("주문의 타입이 DELIVERY 라면, 주소를 필수로 입력받는다.")
	@Test
	void create_ifDeliveryTypeAddressIsNecessary() {
		추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴 '4' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.when("'추천메뉴주문정보' 주문메뉴 정보로 '주소가 없는 배달주문' 주문을 생성하면")
			.then("주문이 실패한다")
			.assertStart();
	}

	@DisplayName("주문의 타입이 EAT_IN이라면, 주문 테이블을 필수로 입력 받는다.")
	@Test
	void create_ifEat_inTypeOrderTableNecessary() {
		추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴 '4' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.when("'추천메뉴주문정보' 주문메뉴 정보로 '홀주문' 주문을 생성하면")
			.then("주문이 실패한다")
			.assertStart();
	}

	@DisplayName("주문 테이블의 상태는 occupied상태여야 한다.")
	@Test
	void create_orderTableStatusMustBeOccupied() {
		추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴 '-1' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.given("'테이블1' 주문 테이블을 생성하고")
			.when("'테이블1' 주문 테이블과 '추천메뉴주문정보' 주문메뉴 정보로 '홀주문' 주문을 생성하면")
			.then("주문이 실패한다")
			.assertStart();
	}

	@DisplayName("주문을 수락한다")
	@Test
	void accept() {
		배달_주문을_생성하고()
			.when("'배달주문' 주문을 수락하면")
			.then("주문의 상태는 'ACCEPTED' 이다")
			.assertStart();
	}

	@DisplayName("주문을 수락하기 위해서는 주문의 상태는 WAITING 상태여야 한다.")
	@Test
	void accept_orderStatusMustWaiting() {
		배달_주문을_생성하고()
			.given("'배달주문' 주문을 수락하고")
			.when("'배달주문' 주문을 수락하면")
			.then("주문 수락에 실패한다")
			.assertStart();
	}

	@DisplayName("주문 타입이 DELIVER라면, 라이더 시스템으로 배달 요청을 전송한다.")
	@Test
	void accept_ifDeliveryTypeSendDeliveryRequestToRiderSystem() {
		배달_주문을_생성하고()
			.when("'배달주문' 주문을 수락하면")
			.then("배달 요청을 전송한다")
			.assertStart();
	}

	@DisplayName("주문 상태는 SERVED 로 변경된다.")
	@Test
	void serve() {
		배달_주문을_생성하고()
			.given("'배달주문' 주문을 수락하고")
			.when("'배달주문' 주문을 서빙하면")
			.then("주문의 상태는 'SERVED' 이다")
			.assertStart();
	}

	@DisplayName("주문 상태가 ACCEPTED 상태여야 한다.")
	@Test
	void serve_mustAcceptedStatus() {
		배달_주문을_생성하고()
			.when("'배달주문' 주문을 서빙하면")
			.then("주문 서빙에 실패한다")
			.assertStart();
	}

	@DisplayName("주문 상태는 DELIVERING 으로 변경된다")
	@Test
	void startDelivery() {
		배달_주문을_생성하고()
			.given("'배달주문' 주문을 수락하고")
			.given("'배달주문' 주문을 서빙하고")
			.when("'배달주문' 주문 배달을 시작하면")
			.then("주문의 상태는 'DELIVERING' 이다")
			.assertStart();
	}

	@DisplayName("주문 배송을 시작하기 위해서는 주문은 DELIVERY 타입이어야 한다.")
	@Test
	void startDelivery_mustDeliveryType() {
		홀_주문을_생성하고()
			.given("'홀주문' 주문을 수락하고")
			.given("'홀주문' 주문을 서빙하고")
			.when("'홀주문' 주문 배달을 시작하면")
			.then("주문 배달에 실패한다")
			.assertStart();
	}

	@DisplayName("주문 배송을 시작하기 위해서는 주문은 SERVED 상태여야 한다.")
	@Test
	void startDelivery_mustServedStatus() {
		배달_주문을_생성하고()
			.given("'배달주문' 주문을 수락하고")
			.when("'배달주문' 주문 배달을 시작하면")
			.then("주문 배달에 실패한다")
			.assertStart();
	}

	@DisplayName("주문이 완료되면 주문 상태는 DELIVERED 로 변경된다.")
	@Test
	void completeDelivery() {
		배달_주문을_생성하고()
			.given("'배달주문' 주문을 수락하고")
			.given("'배달주문' 주문을 서빙하고")
			.given("'배달주문' 주문 배달을 시작하고")
			.when("'배달주문' 배달 완료가 되면")
			.then("주문의 상태는 'DELIVERED' 이다")
			.assertStart();
	}

	@DisplayName("배달주 완료하기 위해서는 주문 상태는 DELIVERING 이어야 한다.")
	@Test
	void completeDelivery_orderStatusMustDelivering() {
		배달_주문을_생성하고()
			.given("'배달주문' 주문을 수락하고")
			.given("'배달주문' 주문을 서빙하고")
			.when("'배달주문' 배달 완료가 되면")
			.then("배달 완료에 실패한다")
			.assertStart();
	}

	@DisplayName("주문을 완료하기 위해 주문이 DELIVERY 타입이면, DELIVERED 상태여야 한다.")
	@Test
	void complete_DELIVERY() {
		배달_주문을_생성하고()
			.given("'배달주문' 주문을 수락하고")
			.given("'배달주문' 주문을 서빙하고")
			.given("'배달주문' 주문 배달을 시작하고")
			.given("'배달주문' 배달 완료가 되고")
			.when("'배달주문' 주문을 종료하면")
			.then("주문의 상태는 'COMPLETED' 이다")
			.assertStart();
	}

	@DisplayName("주문을 완료하기 위해 주문이 EAT_IN 타입이면, DELIVERED 상태여야 한다.")
	@Test
	void complete_EAT_IN() {
		홀_주문을_생성하고()
			.given("'홀주문' 주문을 수락하고")
			.given("'홀주문' 주문을 서빙하고")
			.when("'홀주문' 주문을 종료하면")
			.then("주문의 상태는 'COMPLETED' 이다")
			.assertStart();
	}

	@DisplayName("주문을 완료하기 위해 주문이 TAKEOUT 타입이면, DELIVERED 상태여야 한다.")
	@Test
	void complete_TAKEOUT() {
		포장_주문을_생성하고()
			.given("'포장주문' 주문을 수락하고")
			.given("'포장주문' 주문을 서빙하고")
			.when("'포장주문' 주문을 종료하면")
			.then("주문의 상태는 'COMPLETED' 이다")
			.assertStart();
	}

	@DisplayName("주문을 완료하기 위해 주문이 DELIVERY 타입이면, DELIVERED 상태여야 한다. - 실패")
	@Test
	void complete_DELIVERY_fail() {
		배달_주문을_생성하고()
			.given("'배달주문' 주문을 수락하고")
			.given("'배달주문' 주문을 서빙하고")
			.given("'배달주문' 주문 배달을 시작하고")
			.when("'배달주문' 주문을 종료하면")
			.then("주문 완료에 실패한다")
			.assertStart();
	}

	@DisplayName("주문을 완료하기 위해 주문이 EAT_IN 타입이면, DELIVERED 상태여야 한다. - 실패")
	@Test
	void complete_EAT_IN_fail() {
		홀_주문을_생성하고()
			.given("'홀주문' 주문을 수락하고")
			.when("'홀주문' 주문을 종료하면")
			.then("주문 완료에 실패한다")
			.assertStart();
	}

	@DisplayName("주문을 완료하기 위해 주문이 TAKEOUT 타입이면, DELIVERED 상태여야 한다. - 실패")
	@Test
	void complete_TAKEOUT_fail() {
		포장_주문을_생성하고()
			.given("'포장주문' 주문을 수락하고")
			.when("'포장주문' 주문을 종료하면")
			.then("주문 완료에 실패한다")
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

	private TestGlue.Builder 포장_주문을_생성하고() {
		return 추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴 '4' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.given("'테이블1' 주문 테이블을 생성하고")
			.given("'테이블1' 주문 테이블에 손님이 앉고")
			.when("'테이블1' 주문 테이블과 '추천메뉴주문정보' 주문메뉴 정보로 '포장주문' 주문을 생성하고");
	}

	private TestGlue.Builder 배달_주문을_생성하고() {
		return 추천_메뉴를_생성하고()
			.given("'추천메뉴' 메뉴 '4' 개로 '추천메뉴주문정보' 주문메뉴정보를 생성하고")
			.when("'추천메뉴주문정보' 주문메뉴 정보로 '배달주문' 주문을 생성하고");
	}
}
