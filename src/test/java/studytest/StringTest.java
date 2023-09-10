package studytest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

public class StringTest {

    @DisplayName("isBlank는 null일때 NPE가 발생한다.")
    @NullSource
    @ParameterizedTest
    void isBlack_with_null(String givenText) {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() -> givenText.isBlank());
    }

    @DisplayName("isBlank는 빈 문자열일때 참을 반환한다.")
    @EmptySource
    @ParameterizedTest
    void isBlack_with_emptyString(String givenText) {
        assertTrue(givenText.isBlank());
    }

}
