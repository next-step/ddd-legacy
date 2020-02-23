package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Geonguk Han
 * @since 2020-02-06
 */
class CarTest {

    @Test
    @DisplayName("자동차를 생성한다. 성공")
    void name() {
        final Car andrew = new Car("json");
        assertThat(andrew).isNotNull();
    }

    @DisplayName("자동차 이름이 5글자를 넘지 않는다.")
    @ParameterizedTest
    @ValueSource(strings = {"동해물과 백두", "산이 마르고 닳도록"})
    void create(final String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car(name));
    }

    @DisplayName("자동차가 움직인다.")
    @Test
    void move() {
        final Car car = new Car("andy");
//        car.move(new TestMovingStrategy());
        // method를 하나만 가지는 것을 functional interface
        // 여러개가 추가되면, @FunctionalInterface를 붙여주면 Compile 타입에서 에러가남
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);
    }
}
