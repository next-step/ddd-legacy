package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

/*
    racingcar 패키지의 Car에 대한 테스트 코드를 작성하며 JUnit 5에 대해 학습한다.
    자동차 이름은 5 글자를 넘을 수 없다.
    5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
    자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
*/
class CarTest {
    @DisplayName("자동차 이름은 5글자 이하이다.")
    @Test
    void constructor() {
        assertThatCode(() -> new Car("자동차"))
                .doesNotThrowAnyException();

    }

    @DisplayName("자동차 이름은 비어 있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void constructor_with_null_and_empty_name(final String carName) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car(carName));

    }

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor_illegal() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과 백두산이"));
    }

    @DisplayName("자동차는 움직일 수 있는 경우 이동한다.")
    @Test
    void move() {
        final Car car = new Car("자동차", 0);
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }


    @DisplayName("자동차는 움직일 수 없는 경우 정지한다.")
    @Test
    void stop() {
        final Car car = new Car("자동차", 0);
        car.move(new HoldStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
