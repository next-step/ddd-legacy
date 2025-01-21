package racingcar;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CarTest {

    @ParameterizedTest
    @CsvSource(value = {
            "4, 1",
            "3, 0"
    })
    @DisplayName("자동차는 조건에 따라 움직이거나 움직이지 않는다.")
    void move_or_not(int conditionNumber, int position) {
        // given
        MoveStrategy givenStrategy = (condition) -> condition >= 4;
        CarName givenCarName = new CarName("그렌져");
        Car car = new Car(givenCarName, givenStrategy);

        // when
        car.race(conditionNumber);

        // then
        assertThat(car).isEqualTo(new Car(givenCarName, givenStrategy, position));
    }
}
