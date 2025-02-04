package racingcar;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor() {
        String overFiveLengthName = "일이삼사오육";
        assertThatThrownBy(() -> {
            new Car(overFiveLengthName);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}