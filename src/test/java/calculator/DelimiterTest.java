package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DelimiterTest {

    @Test
    @DisplayName("기본 구분자는 :와 , 이다")
    void initDelimiter() {
        Delimiter delimiter = new Delimiter(new TargetString(""));
        assertThat(delimiter.contains(':')).isTrue();
        assertThat(delimiter.contains(',')).isTrue();
    }

    @Test
    @DisplayName("임의의 조건으로 시작하면 구분자를 추가한다")
    void addNewDelimiter() {
        Delimiter delimiter = new Delimiter(new TargetString("//A\n2A2=4"));
        assertThat(delimiter.contains('A')).isTrue();
    }
}