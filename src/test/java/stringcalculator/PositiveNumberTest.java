package stringcalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PositiveNumberTest {


    @DisplayName("Number 생성, Null 혹은 빈 값 입력 후 비교")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        assertThat(PositiveNumber.of(text).number()).isEqualTo(0);
    }

    @DisplayName("Number 생성, 값 입력 후 비교")
    @ParameterizedTest
    @ValueSource(strings = {"4"})
    void constructorTest(final String text) {
        assertThat(PositiveNumber.of(text).number()).isEqualTo(4);
    }

    @DisplayName("새로 재정의한 equals 기능 정상작동 확인")
    @ParameterizedTest
    @ValueSource(strings = {"4"})
    void overrideEqualTest(final String text) {
        assertThat(PositiveNumber.of(text)).isEqualTo(PositiveNumber.of("4"));
    }

    @DisplayName("새로 재정의한 hashCode 기능 정상작동 확인")
    @ParameterizedTest
    @ValueSource(strings = {"4"})
    void overrideHashCodeTest(final String text) {
        assertThat(PositiveNumber.of(text).hashCode()).isEqualTo(PositiveNumber.of("4").hashCode());
    }

}