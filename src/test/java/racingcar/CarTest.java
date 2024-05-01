package racingcar;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class CarTest {
	@Test
	@DisplayName("자동차 이름은 5자 이하만 가능하다.")
	void test() {
		Assertions.assertThatIllegalArgumentException()
			.isThrownBy(() -> new Car("junbo-car"));
	}

	@ParameterizedTest
	@MethodSource("fourToNine")
	@DisplayName("숫자가 4 이상이면 전진한다")
	void fourToNineTest(Integer condition) {
		Car car = new Car("kim");
		car.move(new ConditionStrategy(condition));
		assertThat(car.position()).isEqualTo(1);
	}

	@ParameterizedTest
	@MethodSource("oneToThree")
	@DisplayName("숫자가 4 미만이면 움직이지 않는다")
	void oneToThreeTest(Integer condition) {
		Car car = new Car("kim");
		car.move(new ConditionStrategy(condition));
		assertThat(car.position()).isEqualTo(0);
	}

	@Test
	@DisplayName("움직이는 상황에서 움직인다")
	void goTest() {
		Car car = new Car("kim");
		car.move(new GoStrategy());
		assertThat(car.position()).isEqualTo(1);
	}

	@Test
	@DisplayName("움직이지 않을 상황에서는 움직이지 않는다")
	void stopTest() {
		Car car = new Car("kim");
		car.move(new StopStrategy());
		assertThat(car.position()).isEqualTo(0);
	}

	static List<Integer> fourToNine() {
		return Arrays.asList(4, 5, 6, 7, 8, 9);
	}

	static List<Integer> oneToThree() {
		return Arrays.asList(1, 2, 3);
	}
}
