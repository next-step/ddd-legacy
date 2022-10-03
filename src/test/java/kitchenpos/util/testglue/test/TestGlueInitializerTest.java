package kitchenpos.util.testglue.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import kitchenpos.util.testglue.Operation;
import kitchenpos.util.testglue.TestGlueConfiguration;
import kitchenpos.util.testglue.TestGlueInitializer;
import kitchenpos.util.testglue.TestGlueOperationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class TestGlueInitializerTest {

	private AnnotationConfigApplicationContext context;
	private TestGlueOperationContext testGlueOperationContext;
	private TestConfiguration1 testConfiguration1;
	private TestConfiguration2 testConfiguration2;
	private TestConfiguration3 testConfiguration3;

	@BeforeEach
	void setUp() {
		context = mock(AnnotationConfigApplicationContext.class);
		testGlueOperationContext = new TestGlueOperationContext();
		testConfiguration1 = new TestConfiguration1();
		testConfiguration2 = new TestConfiguration2();
		testConfiguration3 = new TestConfiguration3();

		given(context.getBean(TestGlueOperationContext.class)).willReturn(testGlueOperationContext);
		given(context.getBeanNamesForAnnotation(TestGlueConfiguration.class)).willReturn(new String[]{"a", "b", "c"});
		given(context.getBean("a")).willReturn(testConfiguration1);
		given(context.getBean("b")).willReturn(testConfiguration2);
		given(context.getBean("c")).willReturn(testConfiguration3);
	}

	@DisplayName("testGlue 초기화 시 operationContext에 Operation이 등록 되는지 확인한다.")
	@Test
	void testGlueInitialize() {
		// given
		var testGlueInitializer = new TestGlueInitializer();

		// when
		testGlueInitializer.setApplicationContext(context);

		Operation test1 = testGlueOperationContext.get("test1");
		Operation test2 = testGlueOperationContext.get("test2");
		Operation test3 = testGlueOperationContext.get("test3");
		test1.run(new String[] {});
		test2.run(new String[] {});
		test3.run(new String[] {});

		// then
		assertAll(
			() -> assertThat(testConfiguration1.getA()).isOne(),
			() -> assertThat(testConfiguration2.getA()).isOne(),
			() -> assertThat(testConfiguration3.getA()).isOne()
		);
	}

	@DisplayName("파싱한 메서드가 실행되는지 확인한다.")
	@Test
	void testGlueMethodRun() {
		var testGlueInitializer = new TestGlueInitializer();

		// when
		testGlueInitializer.setApplicationContext(context);

		// then
		assertAll(
			() -> assertThat(testGlueOperationContext.get("test1")).isNotNull(),
			() -> assertThat(testGlueOperationContext.get("test2")).isNotNull(),
			() -> assertThat(testGlueOperationContext.get("test3")).isNotNull()
		);
	}
}
