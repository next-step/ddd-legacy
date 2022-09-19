package kitchenpos.util.testglue.test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestGlueResponse<T> {

	private final Exception e;
	private final T data;

	private TestGlueResponse(Exception e, T data) {
		this.e = e;
		this.data = data;
	}

	public static <D> TestGlueResponse<D> ok(D data) {
		return new TestGlueResponse<D>(null, data);
	}

	public static <D> TestGlueResponse<D> exception(Exception e) {
		return new TestGlueResponse<>(e, null);
	}

	public boolean isOk() {
		return data != null;
	}

	public T getData() {
		assertThat(isOk()).isTrue();
		return data;
	}

	public Exception getException() {
		assertThat(isOk()).isFalse();
		return e;
	}
}
