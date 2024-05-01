package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

	@DisplayName("자동차는 power가 4 이상인 경우에만 움직인다.")
	@ParameterizedTest
	@ValueSource(ints = {4, 5, 6, 7, 8, 9})
	void move(final int power) {
		assertThat(new Car("백승윤").move(power).getPosition())
			.isEqualTo(1);
	}

	@DisplayName("자동차는 power가 3 이하인 경우에는 움직이지 않는다.")
	@ParameterizedTest
	@ValueSource(ints = {1, 2, 3})
	void notMove(final int power) {
		assertThat(new Car("백승윤").move(power).getPosition())
			.isEqualTo(0);
	}

	@DisplayName("MovingStrategy가 움직일 수 있는 경우 자동차는 움직인다.")
	@Test
	void moveByMovingStrategy() {
		final MovingStrategy goStrategy = () -> true;

		assertThat(new Car("name").move(goStrategy).getPosition())
			.isEqualTo(1);
	}

	@DisplayName("MovingStrategy가 움직일 수 없는 경우 자동차는 움직인다.")
	@Test
	void notMoveByMovingStrategy() {
		MovingStrategy stopStrategy = () -> false;

		assertThat(new Car("name").move(stopStrategy).getPosition())
			.isEqualTo(0);
	}
}
