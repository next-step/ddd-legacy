package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CarTest {

    @DisplayName("자동차는 이름을 가지고 있다")
    @Test
    void test1() {
        final var john = new Car("John");
        assertThat(john.getName()).isEqualTo("John");
    }

    @DisplayName("자동차의 이름이 5글자를 넘으면 예외가 발생한다")
    @Test
    void test2() {
        assertThatThrownBy(
            () -> new Car("Over5Letterse")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 무작위 값이 4 이상인 경우 전진한다")
    @ParameterizedTest
    @ValueSource(ints = {4, 5, 6, 7, 8, 9})
    void test3(int condition) {
        //given
        Car car = new Car("john");
        NumberMoveCondition over4NumberMoveCondition = new NumberMoveCondition(() -> condition);

        //when
        car.move(over4NumberMoveCondition);

        //then
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 무작위 값이 4 미만인 경우 이동하지 않는다.")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void test4(int condition) {
        //given
        Car car = new Car("john");
        NumberMoveCondition under4NumberMoveCondition = new NumberMoveCondition(() -> condition);

        //when
        car.move(under4NumberMoveCondition);

        //then
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
