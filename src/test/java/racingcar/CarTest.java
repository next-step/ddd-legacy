package racingcar;

import static org.assertj.core.api.Assertions.*;

class CarTest {
    @DisplayName("자동차의 이름이 5글자를 초과하면 예외가 발생")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"));
    }
}