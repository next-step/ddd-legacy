package racingcar;


import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CarTest {

    @Test
    @DisplayName("자동차 이름은 5자를 넘길수 없다.")
    void constructor(){
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("nameiscar"));
    }
}