package racingcar;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CarTest {

    @Test
    @DisplayName("제약조건 1: 이름이 5글자가 넘을 수 없다")
    public void checkTheNameConstraint() {
        assertThatThrownBy(
                () -> new Car("아주멋진차량 과학5호기")
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
