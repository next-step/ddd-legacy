package racingcar;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

/**
 * racingcar 패키지의 Car에 대한 테스트 코드를 작성하며 JUnit 5에 대해 학습한다.
 * 자동차 이름은 5 글자를 넘을 수 없다.
 * 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
 * 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
 */
class CarTest {

	@Test
	@DisplayName("자동차 이름은 5 글자 이하만 허용한다")
	void construct() {
		// given
		String name = "ryu";

		// then
		assertThatCode(() -> new Car(name))
			.doesNotThrowAnyException();
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("자동차 이름은 비어있을 수 없다")
	void constructFailByBlankName(final String name) {
		// then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> new Car(name));
	}

	@Test
	@DisplayName("자동차 이름은 5 글자를 넘을 수 없다")
	void constructFailByLongName() {
		// given
		String name = "동해물과 백두산이";

		// then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> new Car(name))
			.withMessage("자동차 이름은 5 글자를 넘을 수 없습니다");
	}

	@Test
	@DisplayName("MovingStrategy TRUE 인 경우 움직인다.")
	void move() {
		// given
		final Car car = new Car("ryu", 0);

		// when
		car.move(new ForwardStrategy());

		// then
		assertThat(car.getPosition()).isEqualTo(1);
	}

	@Test
	@DisplayName("람다를 사용해도 움직인다.")
	void moveByLambda() {
		// given
		final Car car = new Car("ryu", 0);

		// when
		car.move(() -> true);

		// then
		assertThat(car.getPosition()).isEqualTo(1);
	}

	@Test
	@DisplayName("MovingStrategy FALSE 인 경우 움직이지 않는다.")
	void moveFailByRandomValue() {
		// given
		final Car car = new Car("ryu", 0);

		// when
		car.move(new StopStrategy());

		// then
		assertThat(car.getPosition()).isEqualTo(0);
	}
}
