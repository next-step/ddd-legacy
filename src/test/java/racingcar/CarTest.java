package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarTest {

    @DisplayName("자동차를 생성한다.")
    @Test
    void create() {
        final Car car = new Car("자동차");
        assertThat(car).isNotNull();
    }

    @DisplayName("자동차의 이름이 5글자가 넘으면 IllegalArgumentException을 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {"다섯글자에욤", "와싱기방기하네요"})
    void nameValidation(final String name) {
        assertThrows(IllegalArgumentException.class, () -> new Car(name));
    }

    @DisplayName("자동차 움직이는 것 테스트 이동 값이 4이상이면 움직인다")
    @Test
    void move() {
        final Car car = new Car("자동차");
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);
    }
}