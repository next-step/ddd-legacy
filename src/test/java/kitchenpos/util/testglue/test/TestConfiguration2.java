package kitchenpos.util.testglue.test;

import kitchenpos.util.testglue.TestGlueConfiguration;
import kitchenpos.util.testglue.TestGlueOperation;

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
