package kitchenpos.util.testglue;

import java.util.function.Supplier;
import kitchenpos.util.testglue.test.TestGlueResponse;

public class TestGlueSupport implements TestGlueContextAware {

	private TestGlueValueContext testGlueValueContext;

	public void put(String key, Object value) {
		testGlueValueContext.put(key, value);
	}

	public <T> T getAsType(String key, Class<T> type) {
		return testGlueValueContext.getAsType(key, type);
	}

	@Override
	public void testGlueContext(TestGlueValueContext testGlueValueContext) {
		this.testGlueValueContext = testGlueValueContext;
	}

	public TestGlueResponse<?> createResponse(Runnable runnable) {
		try {
			return TestGlueResponse.ok(new Object());
		} catch (Exception e) {
			return TestGlueResponse.exception(e);
		}
	}

	public <T> TestGlueResponse<T> createResponse(Supplier<T> supplier) {
		try {
			T response = supplier.get();
			return TestGlueResponse.ok(response);
		} catch (Exception e) {
			return TestGlueResponse.exception(e);
		}
	}
}
