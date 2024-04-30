package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {
	
	@Test
	@DisplayName("자동차 이름은 5글자를 넘을 수 없다")
	void constructor() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new Car("동해물과백두산이"));
	}
	
	@Test
	@DisplayName("숫자가 4 이상인 경우 자동차는 전진한다")
	public void move() {
		final Car car = new Car("홍길동");
		car.move(() -> true);
		assertThat(car.position()).isEqualTo(1);
	}
	
	@Test
	@DisplayName("숫자가 4 미만인 경우 자동차는 움직이지 않는다")
	public void stop() {
		final Car car = new Car("홍길동");
		car.move(() -> false);
		assertThat(car.position()).isEqualTo(0);
	}
}
