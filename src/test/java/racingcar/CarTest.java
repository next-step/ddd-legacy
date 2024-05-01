package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("자동차 도메인 테스트")
public class CarTest {

    @Test
    @DisplayName("자동차의 이름은 5글자를 넘을 수 없다.")
    void handleNameLength() {
        String name = "abcdef";
        Assertions.assertThatThrownBy(
                () -> new Car(name)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    class Car {
        private String name;

        public Car(String name) {
            handleNameLength(name);
            this.name = name;
        }

        private void handleNameLength(String name) {
            if (5 < name.length()) {
                throw new IllegalArgumentException("자동차 이름은 5 글자를 넘을 수 없습니다.");
            }
        }
    }
}