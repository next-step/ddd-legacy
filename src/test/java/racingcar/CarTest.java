package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("자동차 테스트")
public class CarTest {

    @DisplayName("자동차 이름은 5글자를 초과할 수 없다")
    @Test
    void constructor() {
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("123456"));
    }
}
