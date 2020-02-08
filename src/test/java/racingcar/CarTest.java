package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CarTest {

    @Test
    @DisplayName("자동차가 생성된다.")
    void createCarWithName(){
        final Car car = new Car("abcde");
        assertThat(car).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdef", "abcedfg"})
    @DisplayName("자동차 이름이 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.")
    void carNameShouldNotBeOverFiveChar(final String name){
        assertThatThrownBy(() -> new Car(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 움직인다.")
    @Test
    void moveCar(){
        final int carPosition = 0;
        final Car car = new Car("abcde", carPosition);

        car.move(() -> true);
        assertThat(car.getPosition()).isNotEqualTo(carPosition);
    }

}
