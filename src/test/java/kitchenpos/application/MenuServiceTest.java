package kitchenpos.application;

import kitchenpos.util.testglue.EnableTestGlue;
import kitchenpos.util.testglue.TestGlue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableTestGlue
class MenuServiceTest {

	@Autowired
	private TestGlue testGlue;

	@DisplayName("메뉴를 생성한다.")
	@Test
	void create() {
		testGlue.builder()
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '추천메뉴' 메뉴 데이터를 만들고")
			.when("'추천메뉴' 메뉴 생성을 요청하면")
			.then("'추천메뉴' 메뉴가 생성된다")
			.assertStart();
	}

	@DisplayName("메뉴 가격은 비어있을 수 없다.")
	@Test
	void create_emptyPrice() {
		testGlue.builder()
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '가격이 빈 메뉴' 메뉴 데이터를 만들고")
			.when("'가격이 빈 메뉴' 메뉴 생성을 요청하면")
			.then("'가격이 빈 메뉴' 메뉴 생성에 실패한다")
			.assertStart();
	}

	@DisplayName("메뉴 가격은 음수일 수 없다.")
	@Test
	void create_negativePrice() {
		testGlue.builder()
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '가격이 음수인 메뉴' 메뉴 데이터를 만들고")
			.when("'가격이 음수인 메뉴' 메뉴 생성을 요청하면")
			.then("'가격이 음수인 메뉴' 메뉴 생성에 실패한다")
			.assertStart();
	}

	@DisplayName("메뉴는 특정 메뉴 그룹에 항상 속해야 한다.")
	@Test
	void create_notIncludedInMenuGroup() {
		testGlue.builder()
			.given("'상품1' 상품을 생성하고")
			.given("'없는 그룹'에 속하고 '상품1' '3'개를 이용해 '메뉴그룹이 없는 메뉴' 메뉴 데이터를 만들고")
			.when("'메뉴그룹이 없는 메뉴' 메뉴 생성을 요청하면")
			.then("'메뉴그룹이 없는 메뉴' 메뉴 생성에 실패한다")
			.assertStart();
	}

	@DisplayName("메뉴에 속한 상품은 모두 존재해야 한다.")
	@Test
	void create_get_N_menuProduct() {
		testGlue.builder()
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '없는상품' '3'개를 이용해 '메뉴 상품이 없는 메뉴' 메뉴 데이터를 만들고")
			.when("'메뉴 상품이 없는 메뉴' 메뉴 생성을 요청하면")
			.then("'메뉴 상품이 없는 메뉴' 메뉴 생성에 실패한다")
			.assertStart();
	}

	@DisplayName("메뉴의 가격은 각 상품의 가격 * 각 상품의 재고 수 보다 클 수 없다.")
	@Test
	void create_menu_price_constraint() {
		testGlue.builder()
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '비싼메뉴' 메뉴 데이터를 만들고")
			.when("'비싼메뉴' 메뉴 생성을 요청하면")
			.then("'비싼메뉴' 메뉴 생성에 실패한다")
			.assertStart();
	}

	@DisplayName("메뉴 이름은 비어있을 수 없다.")
	@Test
	void create_empty_name() {
		testGlue.builder()
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '이름이 빈 메뉴' 메뉴 데이터를 만들고")
			.when("'이름이 빈 메뉴' 메뉴 생성을 요청하면")
			.then("'이름이 빈 메뉴' 메뉴 생성에 실패한다")
			.assertStart();
	}

	@DisplayName("메뉴 이름에는 욕설이 포함될 수 없다.")
	@Test
	void create_2() {
		testGlue.builder()
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '욕설이 포함된 메뉴' 메뉴 데이터를 만들고")
			.when("'욕설이 포함된 메뉴' 메뉴 생성을 요청하면")
			.then("'욕설이 포함된 메뉴' 메뉴 생성에 실패한다")
			.assertStart();
	}

	@DisplayName("가격은 비어있을 수 없다.")
	@Test
	void changePrice_empty() {
		testGlue.builder()
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '추천메뉴' 메뉴 데이터를 만들고")
			.given("'추천메뉴' 메뉴를 생성하고")
			.when("'추천메뉴' 메뉴 가격을 '' 로 변경하면")
			.then("'추천메뉴' 메뉴 가격 변경에 실패한다")
			.assertStart();
	}

	@DisplayName("가격은 음수일 수 없다.")
	@Test
	void changePrice_negative() {
		testGlue.builder()
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '추천메뉴' 메뉴 데이터를 만들고")
			.given("'추천메뉴' 메뉴를 생성하고")
			.when("'추천메뉴' 메뉴 가격을 '-10000' 로 변경하면")
			.then("'추천메뉴' 메뉴 가격 변경에 실패한다")
			.assertStart();
	}

	@DisplayName("가격을 변경하고자 하는 메뉴는 반드시 존재해야 한다.")
	@Test
	void changePrice_exist_menu() {
		testGlue.builder()
			.when("없는메뉴 메뉴 가격을 '20000' 로 변경하면")
			.then("없는메뉴 메뉴 가격 변경에 실패한다")
			.assertStart();
	}

	@DisplayName("변경하고자 하는 가격이 현재 가격과 같아선 안된다.")
	@Test
	void changePrice() {
		testGlue.builder()
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '추천메뉴' 메뉴 데이터를 만들고")
			.given("'추천메뉴' 메뉴를 생성하고")
			.when("'추천메뉴' 메뉴 가격을 '-10000' 로 변경하면")
			.then("'추천메뉴' 메뉴 가격 변경에 실패한다")
			.assertStart();
	}

	@DisplayName("상품을 전시상태로 변경한다.")
	@Test
	void display() {
		testGlue.builder()
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '추천메뉴' 메뉴 데이터를 만들고")
			.given("'추천메뉴' 메뉴를 생성하고")
			.when("'추천메뉴' 메뉴를 전시상태로 변경하면")
			.then("'추천메뉴' 메뉴가 전시상태로 변경된다")
			.assertStart();
	}

	@DisplayName("상품을 전시상태로 변경에 실패한다.")
	@Test
	void display_fail() {
		testGlue.builder()
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '추천메뉴' 메뉴 데이터를 만들고")
			.given("'추천메뉴' 메뉴를 생성하고")
			.when("'추천메뉴' 메뉴를 전시상태로 변경하면")
			.then("'추천메뉴' 메뉴가 전시상태로 변경된다")
			.assertStart();
	}

	@DisplayName("상품을 미전시상태로 변경한다.")
	@Test
	void hide() {
		testGlue.builder()
			.given("'추천메뉴그룹' 메뉴 그룹을 생성하고")
			.given("'상품1' 상품을 생성하고")
			.given("'추천메뉴그룹'에 속하고 '상품1' '3'개를 이용해 '추천메뉴' 메뉴 데이터를 만들고")
			.given("'추천메뉴' 메뉴를 생성하고")
			.when("'추천메뉴' 메뉴를 미전시상태로 변경하면")
			.then("'추천메뉴' 메뉴가 미전시상태로 변경된다")
			.assertStart();
	}
}
