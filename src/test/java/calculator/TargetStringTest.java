package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TargetStringTest {

    @Test
    @DisplayName("문자 열이 정해진 규칙(새로 구분 자를 추가) 시작하는 경우 새 구분 자를 반환")
    void getDelimiterOrNullTest() {
        TargetString targetString = new TargetString("//!\n131!313");
        Optional<Character> actual = targetString.getDelimiterOrNull();

        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo('!');
    }
}