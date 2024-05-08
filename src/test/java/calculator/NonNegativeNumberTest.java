package calculator;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NonNegativeNumberTest {

	@DisplayName("음수가 입력되면 예외를 반환한다.")
	@Test
	void negativeThrowException() {
		assertThatCode(() -> new NonNegativeNumber(-1))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Negative number not allowed: -1");
	}

	@DisplayName("숫자가 아닌 문자가 입력되면 예외를 반환한다.")
	@Test
	void invalidCharacter() {
		assertThatCode(() -> NonNegativeNumber.from("^"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Character not allowed: ^");
	}
}
