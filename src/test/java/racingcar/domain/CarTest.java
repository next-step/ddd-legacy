package racingcar.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CarTest {

	@ParameterizedTest
	@DisplayName("자동차 이름은 null 값을 가지거나 비어있을 수 없다.")
	@NullAndEmptySource
	@ValueSource(strings = {" ", "  "})
	void constructorWithEmptyCarNames(String carName) {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Car(carName));
	}

	@ParameterizedTest
	@DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
	@ValueSource(strings = {"동해물과백두", "동해물과백두산"})
	void constructorWithCarNamesExceedMaximumLength(String carName) {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Car(carName));
	}

	@ParameterizedTest
	@DisplayName("자동차는 5글자 이하의 이름을 가질 수 있다.")
	@ValueSource(strings = {"동", "동해", "동해물", "동해물과", "동해물과백"})
	void constructor(String carName) {
		assertThatCode(() -> new Car(carName)).doesNotThrowAnyException();
	}
}
