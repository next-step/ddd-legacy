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
class ProductServiceTest_changePrice extends TestIsolationSupport {

	@Autowired
	private TestGlue testGlue;

	@DisplayName("상품의 가격을 변경한다.")
	@Test
	void changePrice() {
		testGlue.builder()
			.given("'정상상품' 상품을 생성하고")
			.when("'정상상품' 상품 가격을 '2000' 으로 변경하면")
			.then("'정상상품' 상품 가격은 변경된다")
			.assertStart();
	}

	@DisplayName("상품의 가격을 null로 변경할 순 없다.")
	@Test
	void changePrice_emptyPrice_null() {
		testGlue.builder()
			.given("'정상상품' 상품을 생성하고")
			.when("'정상상품' 상품 가격을 'null' 으로 변경하면")
			.then("'정상상품' 상품 가격 변경에 실패한다")
			.assertStart();
	}

	@DisplayName("상품의 가격을 비어있게 변경할 순 없다.")
	@Test
	void changePrice_emptyPrice_blank() {
		testGlue.builder()
			.given("'정상상품' 상품을 생성하고")
			.when("'정상상품' 상품 가격을 '' 으로 변경하면")
			.then("'정상상품' 상품 가격 변경에 실패한다")
			.assertStart();
	}

	@DisplayName("상품의 가격을 0보다 작게 변경할 순 없다.")
	@Test
	void changePrice_price_less_then_zero() {
		testGlue.builder()
			.given("'정상상품' 상품을 생성하고")
			.when("'정상상품' 상품 가격을 '-10000' 으로 변경하면")
			.then("'정상상품' 상품 가격 변경에 실패한다")
			.assertStart();
	}

	@DisplayName("존재하지 않는 상품을 변경할 순 없다.")
	@Test
	void changePrice_no_exist() {
		testGlue.builder()
			.when("존재하지 않는 상품 상품 가격을 '10000' 으로 변경하면")
			.then("존재하지 않는 상품 상품 가격 변경에 실패한다")
			.assertStart();
	}

	@DisplayName("상품의 가격을 변경한 뒤, 상품을 들고있는 메뉴들의 가격 제약조건은 성립되어야 한다. 만족하지 않는다면 메뉴를 미전시 상태로 바꾼다.")
	@Test
	void changePrice_menu_constraint() {
		testGlue.builder()
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '추천메뉴' 메뉴 데이터를 만들고")
			.given("'추천메뉴' 메뉴를 생성하고")
			.when("'상품1' 상품 가격을 '1000' 으로 변경하면")
			.then("'추천메뉴' 메뉴가 미전시상태로 변경된다")
			.assertStart();
	}
}
