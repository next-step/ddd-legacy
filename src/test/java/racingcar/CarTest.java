package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarTest {

    @DisplayName("Car 이름의 길이는 5 이하여야 한다")
    @Test
    void carNameLengthShouldBeLessThenOrEqual5() {
        assertThatThrownBy(() -> {
            new Car("aaaaab");
        }).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("Car는 움직일 수 있는 조건을 충족하면 이동할 수 있다")
    @Test
    void carCanMoveWhenMeetOver4() {
        Car rollsRoyce = new Car("롤스로이스");
        rollsRoyce.move(() -> true);
    }
}
