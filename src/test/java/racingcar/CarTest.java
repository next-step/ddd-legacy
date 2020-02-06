package racingcar;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CarTest {

    @Test
    public void construct_when_name_is_over_5() {
        assertThatThrownBy(() -> new Car("다섯글자넘음"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void construct_when_name_is_null() {
        assertThatThrownBy(() -> new Car(null))
            .isInstanceOf(IllegalArgumentException.class);
    }
}