package racingcar;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarNameTest {

    @Test
    @DisplayName("자동차 이름을 생성할 수 있다.")
    void success() {
        // given
        String givenName = "그렌져";

        // when
        CarName carName = new CarName(givenName);

        // then
        carName.equals(new CarName("그렌져"));
    }

    @Test
    @DisplayName("자동차 이름은 5글자를 넘으면 예외가 발생한다.")
    void fail() {
        // given
        String givenName = "굉장한 그렌져";

        // when // then
        assertThatThrownBy(() -> new CarName(givenName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자동차 이름은 5글자를 넘을 수 없다.");
    }
}
