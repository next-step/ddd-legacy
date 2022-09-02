package kitchenpos.utiltest;

import kitchenpos.util.TestGlueConfiguration;
import kitchenpos.util.TestGlueOperation;

@TestGlueConfiguration
public class TestConfiguration2 {

	private int a = 0;

	@TestGlueOperation("test2")
	public void test() {
		a++;
	}

	public int getA() {
		return a;
	}
}
