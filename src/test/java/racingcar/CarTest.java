package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarTest {
    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void validCarNameTest() {
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("abcdefg"));
    }

    @DisplayName("숫자가 4 이상인 경우 자동차는 전진한다.")
    @Test
    void moveTest() {
        final Car car = new Car("보라돌");
        car.move(new GoStrategy());
        assertThat(car.position()).isEqualTo(1);
    }

    @DisplayName("숫자가 4 미만인 경우 자동차는 움직이지 않는다.")
    @Test
    void stopTest() {
        final Car car = new Car("보라돌");
        car.move(new StopStrategy());
        assertThat(car.position()).isEqualTo(0);
    }
}
