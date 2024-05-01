package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CarTest {

	@DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
	@Test
	void constructor() {
		assertThatCode(() -> new Car("123456"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("이름은 5글자를 넘을 수 없습니다");

		assertThatCode(() -> new Car("12345"))
			.doesNotThrowAnyException();
	}


}
