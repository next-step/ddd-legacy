package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CarTest {

	@DisplayName("자동차 이름은 5글자를 넘을 수 없다")
	@Test
	void name() {
		assertThatThrownBy(() -> new Car("여섯글자에요"))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("자동차가 전진한다")
	@Test
	void movable() {
		Car car = new Car("황금마티즈");
		car.move(new MovableStrategy());
		assertThat(car.getPosition()).isEqualTo(1);
	}

	@DisplayName("자동차가 전진하지 않는다")
	@Test
	void immovable() {
		Car car = new Car("황금마티즈");
		car.move(new ImmovableStrategy());
		assertThat(car.getPosition()).isZero();
	}
}
