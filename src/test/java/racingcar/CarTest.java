package racingcar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @DisplayName("5글자가 넘으면 IllegalArgumentException이 발생한다.")
    @Test
    @ValueSource(strings = {"동해물과백두", "산이마르고닳도"})
    void CarLengthOver5() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car("abcdefghijklmn"));
//        Assertions.assertThrows(IllegalArgumentException.class, ()->{
//            new Car("length5", 0);
//        });
    }

    @DisplayName("5글자가 넘으면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"동해물과백두", "산이마르고닳도"})
    void create() {
    }

    @DisplayName("5글자 이하이면 객체가 생성된다.")
    @Test
    void CarLengthUnder5(){
        final Car car = new Car("4444", 0);

        Assertions.assertNotNull(car);
    }

    @DisplayName("Random 값이 4이하면 Position 값이 변하지 않는다.")
    @Test
    void carLengthUnder5AndRandomOver4(){
        final Car car = new Car("4444", 0);
    }

    @Test
    void move(){
        final Car car = new Car("json");
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);
    }
}