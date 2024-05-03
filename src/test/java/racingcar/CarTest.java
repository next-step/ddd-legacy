package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class CarTest {

    @NullAndEmptySource
    @ParameterizedTest
    @DisplayName("자동차 이름은 null이거나 빈값일 수 없다.")
    void carNameDoesNotExist(String carName) {
        assertThatCode(()-> new Car(carName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자동차 이름은 null 이거나 빈 값일 수 없다.");
    }

    @Test
    @DisplayName("자동차 이름은 5글자를 넘으면 IllegalArgumentException이 발생한다.")
    void carNameLengthDoNotOverFiveCharacters() {
        assertThatCode(() -> new Car("bumpercar"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자동차 이름은 5 글자를 넘을 수 없다.");
    }

    @Test
    @DisplayName("무작위 값이 4 이상이면 움직인다.")
    void moveCarRandomNumberOverEqualFour() {
        Car mycar = new Car("mycar");
        mycar.move(new GoStrategy());

        assertThat(mycar.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("무작위 값이 3 이하이면 움직이지 않는다.")
    void noMoveCarRandomNumberUnderEqualThree() {
        Car mycar = new Car("mycar");
        mycar.move(new StopStrategy());

        assertThat(mycar.getPosition()).isEqualTo(0);
    }
}
