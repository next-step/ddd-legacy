package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CarTest {

    @Test
    @DisplayName("자동차는 이름을 가지고 있다.")
    void test1() {
        final var actual = new Car("greg");
        assertThat(actual.getName()).isEqualTo("greg");
    }

    @Test
    @DisplayName("자동차의 이름이 5글자를 넘으면 예외가 발생한다.")
    void test2() {
        assertThatThrownBy(() -> new Car("greg12345"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 5, 6, 7, 8, 9})
    @DisplayName("자동차는 무작위 값이 4 이상일 경우 전진한다.")
    void test3(final int condition) {

        final Car car = new Car("greg");
        car.move(new NumberMoveCondition(condition));
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @DisplayName("자동차는 무작위 값이 3 이하일 경우 정지한다.")
    void test4(final int condition) {
        final Car car = new Car("greg");
        car.move(new NumberMoveCondition(condition));
        assertThat(car.getPosition()).isEqualTo(0);
    }

    @Test
    @DisplayName("자동차는 정지한다.")
    void test5() {
        final Car car = new Car("greg");
        car.move(new StopMoveCondition());
        assertThat(car.getPosition()).isEqualTo(0);
    }

}
