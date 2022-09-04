package kitchenpos.application;

import kitchenpos.util.testglue.EnableTestGlue;
import kitchenpos.util.testglue.TestGlue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@EnableTestGlue
@SpringBootTest
class ProductServiceTest_create {

	@Autowired
	private TestGlue testGlue;

	@DisplayName("상품을 생성한다")
	@Test
	void create() {
		testGlue.builder()
			.given("'정상상품' 데이터를 생성하고")
			.when("'정상상품' 생성을 요청하면")
			.then("'정상상품'이 생성된다")
			.assertStart();
	}

	@DisplayName("상품의 가격은 비어있으면 안된다.")
	@Test
	void create_emtpyPrice() {
		testGlue.builder()
			.given("'가격이 빈 상품' 데이터를 생성하고")
			.when("'가격이 빈 상품' 생성을 요청하면")
			.then("'가격이 빈 상품'이 생성에 실패한다")
			.assertStart();
	}

	@DisplayName("상품의 가격은 음수여서는 안된다.")
	@Test
	void create_negativePrice() {
		testGlue.builder()
			.given("'가격이 음수인 상품' 데이터를 생성하고")
			.when("'가격이 음수인 상품' 생성을 요청하면")
			.then("'가격이 음수인 상품'이 생성에 실패한다")
			.assertStart();
	}
}
