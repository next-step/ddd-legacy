package racingcar;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("car 도메인 테스트")
class CarTest {

	@DisplayName("자동차 생성 테스트")
	@Test
	void createCarTest() {
		assertThatCode(() -> new Car("car"))
			.doesNotThrowAnyException();
	}

	@DisplayName("자동차는 이름을 가질 수 있다.")
	@ParameterizedTest
	@ValueSource(strings = {"car", "car1", "car12"})
	void carHasNameTest(String nameInput) {
		Car car = new Car(nameInput);
		assertThat(car.getName()).isEqualTo(nameInput);
	}

	@DisplayName("자동차의 이름이 5글자를 넘으면 예외가 발생한다.")
	@ParameterizedTest
	@ValueSource(strings = {"carcar", "carcar1", "carcar12", "carcarcar"})
	void carNameLengthTest(String nameInput) {
		assertThatThrownBy(() -> new Car(nameInput))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("자동차 이름은 5글자를 넘을 수 없습니다.");
	}

	@DisplayName("자동차의 이름은 공백이나 null이 아니다.")
	@ParameterizedTest
	@NullAndEmptySource
	void carNameNullAndEmptyTest(String nameInput) {
		assertThatThrownBy(() -> new Car(nameInput))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("자동차는 조건을 만족하면 움직일 수 있다.")
	@Test
	void carMoveTest() {
		Car car = new Car("car");
		car.move(() -> true);
		assertThat(car.getPosition()).isEqualTo(1);
	}

	@DisplayName("자동차는 조건을 만족하지 않으면 움직이지 않는다.")
	@Test
	void carNotMoveTest() {
		Car car = new Car("car");
		car.move(() -> false);
		assertThat(car.getPosition()).isZero();
	}
}
