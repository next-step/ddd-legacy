package racingcar;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CarTest {
	@DisplayName("자동차는 이름을 가지고 있다.")
	@Test
	void name() {
		var car = new Car("제형");
		assertThat(car.getName()).isEqualTo("제형");
	}

	@DisplayName("자동차 이름은 5자리를 넘을 수 없다.")
	@Test
	void length() {
		assertThatThrownBy(() -> new Car("일이삼사오육"))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("자동차가 무작위 4이상인경우 움직인다.")
	@Test
	void a() {
		final Car car = new Car("제형");
		car.move(new NumberMoveCondition(4));
		assertThat(car.getPosition()).isOne();
	}

	@DisplayName("자동차가 무작위 3 이하 인경우 움직인다.")
	@ValueSource(ints = {0, 1, 2, 3})
	@ParameterizedTest
	void b(int value) {
		final Car car = new Car("제형");
		car.move(new NumberMoveCondition(value));
		assertThat(car.getPosition()).isZero();
	}

	@DisplayName("자동차는 정지한다.")
	@Test
	void c() {
		final Car car = new Car("제형");
		car.move(new StopMoveCondition());
		assertThat(car.getPosition()).isZero();
	}
}
