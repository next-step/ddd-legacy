package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CarTest {
    @DisplayName("자동차를 생성한다")
    @Test
    void create() {
        final Car car = new Car("jason");
        assertThat(car).isNotNull();
    }

    /*
        racingcar 패키지의 Car에 대한 테스트 코드를 작성하며 JUnit 5에 대해 학습한다.
        자동차 이름은 5 글자를 넘을 수 없다.
        5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
        자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
     */
    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"동해물과백두", "산이마르고달도록"})
    void name() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    new Car("abcdef");
                    });
    }

    @Test
    void move() {
        Car car = new Car("dddd");
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);
    }

}