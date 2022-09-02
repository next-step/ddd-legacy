package kitchenpos.utiltest;

import kitchenpos.util.TestGlueConfiguration;
import kitchenpos.util.TestGlueOperation;

@TestGlueConfiguration
public class TestConfiguration3 {

	private int a = 0;

	@TestGlueOperation("test3")
	public void test() {
		a++;
	}

	public int getA() {
		return a;
	}
}
