package racingcar.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CarTest {

	@ParameterizedTest
	@DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
	@ValueSource(strings = {"동해물과백두", "동해물과백두산"})
	void constructor(String carName) {
		assertThatIllegalArgumentException().isThrownBy(() -> new Car(carName));
	}
}
