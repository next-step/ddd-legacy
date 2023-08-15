package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class CarTest {
    @DisplayName("자동차는 이름을 가지고 있다")
    @Test
    void test1() {
        final Car car = new Car("boon");

        assertThat(car.getName()).isEqualTo("boon");
    }

    @DisplayName("자동차의 이름이 5글자를 넘으면 예외가 발생한다")
    @Test
    void test2() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("boonsoo"));
    }

    @DisplayName("자동차는 무작위 값이 4 이상인 경우 전진한다")
    @Test
    void test3() {
        final Car car = new Car("boon");

        car.move(4);

        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 무작위 값이 3 이하인 경우 정지한다")
    @Test
    void test4() {
        final Car car = new Car("boon");

        car.move(3);

        assertThat(car.getPosition()).isEqualTo(0);
    }
}
