package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {
    @DisplayName(("자동차의 이름은 5글자 이하다"))
    @Test
    void constructor(){
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(()-> new Car("동해물과백두산이"));
    }
}