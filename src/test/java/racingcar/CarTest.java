package racingcar;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;

class CarTest {

	@DisplayName("자동차는 이름을 가지고 있다")
	@Test
	void name() {
		final var actual = new Car("hello");
		assertThat(actual.getName()).isEqualTo("hello");
	}

	@DisplayName("자동차는 이름이 5글자를 넘으면 예외가 발생한다")
	@Test
	void name2() {
		assertThatThrownBy(() -> new Car("seungjoopet"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("자동차는 무작위 값이 4이상인 경우 전진한다")
	@Test
	void name3() {
		final Car car = new Car("hello");
		car.move(4);
		assertThat(car.getPosition()).isEqualTo(1);
	}

	@DisplayName("자동차는 무작위 값이 3 이하인 경우 정지한다")
	@Test
	void name4() {
		final Car car = new Car("hello");
		car.move(3);
		assertThat(car.getPosition()).isEqualTo(0);
	}

	@DisplayName("자동차는 무작위 값이 3이하인 경우 정지한다")
	@ValueSource(ints = {0, 1, 2, 3})
	@Test
	void name5(final int condition) {
		final Car car = new Car("hello");
		car.move(new NumberMoveCondition(condition));
		assertThat(car.getPosition()).isZero();
	}
}
