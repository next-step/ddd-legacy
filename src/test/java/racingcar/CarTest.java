package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CarTest {

	@Test
	@DisplayName("자동차 이름은 5글자를 넘을수 없다")
	void constructor() {
		assertThatThrownBy(
				() -> new Car("aaaaa")
		).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("자동가 전진한다")
	void go() {
		Car car = new Car("car");
		car.move(new GoMovingStrategy());
		assertThat(car.getPosition()).isEqualTo(1);
	}

	@Test
	@DisplayName("자동가 전진하지 않는다")
	void stop() {
		Car car = new Car("car");
		car.move(new StopMovingStrategy());
		assertThat(car.getPosition()).isEqualTo(0);
	}
}