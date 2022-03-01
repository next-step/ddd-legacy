package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CarTest {

	@DisplayName("자동차 이름은 5글자를 넘을 수 없다")
	@Test
	void constructor() {
		Assertions.assertThatThrownBy(() -> new Car("동해물과백두산이")).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("4 이상인 경우 자동차가 전진한다")
	@Test
	void go() {
		final Car car = new Car("chae-yh", 0);
		car.move(new MustGoStrategy());
		Assertions.assertThat(car.getPosition()).isEqualTo(1);
	}

	@DisplayName("3 이하인 경우 자동차가 움직이지 않는다")
	@ValueSource(ints = {0, 1, 2, 3})
	@ParameterizedTest
	void stop(final int number) {
		final Car car = new Car("chae-yh", 0);
		car.move(number);
		Assertions.assertThat(car.getPosition()).isZero();
	}

	@DisplayName("3 이하인 경우 자동차가 움직이지 않는다")
	@ParameterizedTest
	void stop() {
		final Car car = new Car("chae-yh", 0);
		car.move(new MustStopStrategy());
		Assertions.assertThat(car.getPosition()).isZero();
	}
}
