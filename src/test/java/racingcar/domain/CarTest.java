package racingcar.domain;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CarTest {

    @DisplayName("이름이 기준값 보다 클 경우 예외 발생")
    @ParameterizedTest
    @ValueSource(strings = {"aaaaaa", "invalidName"})
    void createWithInvalidCarName(String nameOfCar) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car(nameOfCar));
    }

}
