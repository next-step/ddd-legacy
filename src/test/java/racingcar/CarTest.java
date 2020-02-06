package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.StringUtils;

import static org.assertj.core.api.Assertions.*;

class CarTest {

    @Test
    @DisplayName("차 생성")
    void createCar() {
        // given
        final Car car = new Car("carna");

        // when

        // then
        assertThat(car).isNotNull();
    }

    @DisplayName("차 이름은 5자초과이면 예외")
    @ParameterizedTest
    @ValueSource(strings = {"aaaaaa", "bbbbbbbb"})
    void carNameShouldNotBeOverFiveChar(final String name) {
        assertThatThrownBy(() -> new Car(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("차 이동")
    void name() {
        // given
        final int position = 0;
        final Car car = new Car("name", position);

        // when
        car.move(() -> true);

        // then
        assertThat(car.getPosition()).isEqualTo(position + 1);
    }
}