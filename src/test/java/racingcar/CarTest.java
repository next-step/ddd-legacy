package racingcar;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {

	@Test
	@DisplayName("자동차 이름이 5글자가 넘어 가는 경우 예외가 발생한다.")
	void pllzekeeh() {
		assertThatThrownBy(() -> new Car("6글자입니다")).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("자동차가 전진한다.")
	void qpwehodqg() {
		//given
		Car car = new Car("안움직임");

		//when
		Car move = car.move(() -> true);

		//then
		assertThat(move).isEqualTo(new Car("안움직임", 1));
	}

	@Test
	@DisplayName("자동차가 전진하지 않는다.")
	void uncgpjmvx() {
		//given
		Car car = new Car("안움직임");

		//when
		Car move = car.move(() -> false);

		//then
		assertThat(move).isEqualTo(new Car("안움직임"));
	}
}