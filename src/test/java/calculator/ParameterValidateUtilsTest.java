package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ParameterValidateUtilsTest {

    private static final String TEST_NAME = "test";
    
    @DisplayName("value가 null이면 예외를 발생시킨다.")
    @Test
    void checkNotNull_exception() {
        // given

        // when & then
        assertThatThrownBy(() -> ParameterValidateUtils.checkNotNull(null, TEST_NAME))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("value가 null이 아니면 그것을 그대로 반환한다.")
    @Test
    void checkNotNull() {
        // given

        // when & then
        // String
        final String dummyString = "test";
        final String actual = ParameterValidateUtils.checkNotNull(dummyString, TEST_NAME);
        final String expectedMemory = dummyString;

        assertThat(actual == expectedMemory).isTrue();
        assertThat(actual).isEqualTo(dummyString);

        // Object
        final Object dummyObject = (Runnable) () -> {
        };
        final Object actual2 = ParameterValidateUtils.checkNotNull(dummyObject, TEST_NAME);
        final Object expectedMemory2 = dummyObject;

        assertThat(actual2 == expectedMemory2).isTrue();
        assertThat(actual2).isEqualTo(dummyObject);
    }
}
