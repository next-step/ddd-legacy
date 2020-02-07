package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static racingcar.Car.MAX_LENGTH_OF_NAME;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CarTest {

    @MethodSource("construct_cases")
    @ParameterizedTest
    @DisplayName("자동차 이름은 " + MAX_LENGTH_OF_NAME + "글자를 넘을 수 없다.")
    public void construct_with_invalid_name(final String name,
                                            final Class<Throwable> expected,
                                            final String description) {
        assertThatThrownBy(() -> new Car(name))
            .as(description)
            .isInstanceOf(expected);
    }

    private static Stream<Arguments> construct_cases() {
        return Stream.of(Arguments.of("다섯글자넘음",
                                      IllegalArgumentException.class,
                                      "자동차의 이름이 " + MAX_LENGTH_OF_NAME + "글자가 넘으면 IllegalArgumentException 을 던진다."),
                         Arguments.of(null,
                                      IllegalArgumentException.class,
                                      "자동차의 이름이 null 이면 IllegalArgumentException 을 던진다."),
                         Arguments.of("   ",
                                      IllegalArgumentException.class,
                                      "자동차의 이름이 공백으로만 구성되면 IllegalArgumentException 을 던진다."));
    }

    @MethodSource("move_cases")
    @ParameterizedTest
    @DisplayName("자동차는 " + Car.POINT_VARIATION_OF_MOVING + "만큼 움직인다.")
    public void move(final int positionFrom,
                     final boolean movable,
                     final int positionTo) {
        Car car = carWith(positionFrom);

        car.move(() -> movable);

        assertThat(car)
            .as("움직일 조건이 %s 이면, 위치는 %s 에서 %s 가 된다.",
                movable,
                positionFrom,
                positionTo)
            .isEqualTo(carWith(positionTo));
    }

    private static Stream<Arguments> move_cases() {
        return Stream.of(Arguments.of(Car.DEFAULT_POSITION,
                                      true,
                                      Car.DEFAULT_POSITION + Car.POINT_VARIATION_OF_MOVING),
                         Arguments.of(Car.DEFAULT_POSITION,
                                      false,
                                      Car.DEFAULT_POSITION));
    }

    private static Car carWith(int position) {
        return new Car("Car", position);
    }
}