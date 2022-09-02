package kitchenpos.utiltest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import kitchenpos.util.EnableTestGlue;
import kitchenpos.util.TestGlue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@EnableTestGlue
@SpringBootTest(classes = {TestConfiguration1.class, TestConfiguration2.class, TestConfiguration3.class})
class TestGlueTest {

	@Autowired
	private TestGlue testGlue;

	@Autowired
	private TestConfiguration1 testConfiguration1;

	@Autowired
	private TestConfiguration2 testConfiguration2;

	@Autowired
	private TestConfiguration3 testConfiguration3;

	@DisplayName("testGlue를 이용하여 build가 제대로 이루어 지는지 확인한다.")
	@Test
	void build() {
		testGlue.builder()
			.given("test1")
			.when("test2")
			.then("test3")
			.assertStart();

		assertAll(
			() -> assertThat(testConfiguration1.getA()).isOne(),
			() -> assertThat(testConfiguration2.getA()).isOne(),
			() -> assertThat(testConfiguration3.getA()).isOne()
		);
	}
}
