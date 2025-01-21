package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;

class CarTest {
    @DisplayName(("자동차의 이름은 5글자 이하다"))
    @Test
    void constructor() {
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @DisplayName(("자동차는 움직인다"))
    @Test
    void move() {
        final var car = new Car("sumin");
        car.move(() -> true);
        Assertions.assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName(("자동차는 정지한다"))
    @Test
    void stop() {
        final var car = new Car("sumin");
        car.move(() -> false);
        Assertions.assertThat(car.getPosition()).isEqualTo(0);
    }
}