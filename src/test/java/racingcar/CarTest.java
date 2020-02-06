package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class CarTest {

    @DisplayName("자동차를 생성한다.")
    @Test
    void create() {
        final Car car = new Car("jason");
        assertThat(car).isNotNull();
    }

    @DisplayName("5 글자가 넘는 경우, IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"123456", "1234567", "12345678"})
    void checkName(String name) {
        assertThatIllegalArgumentException().isThrownBy(() -> {
            new Car(name);
        });

//        assertThatThrownBy(() -> {
//            new Car("123456");
//        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 움직인다.")
    @Test
    void move() {
        final Car car = new Car("tom");
//        car.move(new TestMovingStrategy());
        car.move(() -> true); // 윗줄처럼 가짜객체를 만들어도 되지만, lambda 표현식을 통해 표현할 수도 있다.

        assertThat(car.getPosition()).isEqualTo(1);
    }
}