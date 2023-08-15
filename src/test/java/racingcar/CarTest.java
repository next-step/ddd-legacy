package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @DisplayName("[성공] 자동차 이름이 5글자 이상이어야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"55555", "66666", "가나라다마사아자차카"})
    void carNameLongerThanFive(String input) {
        assertDoesNotThrow(() -> new Car(input));
    }

    @DisplayName("[실패] 자동차 이름이 5글자 미만이면 안된다.")
    @ParameterizedTest
    @ValueSource(strings = {"1", "22", "333", "4444"})
    void carNameShorterThanFive(String input) {
        assertThatThrownBy(() -> new Car(input))
                .isInstanceOf(IllegalAccessException.class);
    }

    @DisplayName("[실패] 자동차 이름이 공백이거나 null일 수 없다.")
    @Test
    @NullAndEmptySource
    void carNameNullAndEmptySource(String input) {
        assertThatThrownBy(() -> new Car(input))
                .isInstanceOf(IllegalAccessException.class);
    }
}