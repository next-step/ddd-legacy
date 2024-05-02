package calculator;

import calculator.domain.Number;
import calculator.domain.SplitStrategy;
import calculator.domain.StringSplitStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("문자열 분리 도메인 테스트")
public class SplitStrategyTest {
    public static final SplitStrategy STRATEGY = new StringSplitStrategy();
    private SplitStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new StringSplitStrategy();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.(예 : “” => 0, null => 0)")
    void checkEmptyAndNull(String input) {
        Assertions.assertThat(strategy.split(input)).containsOnly(new Number(0));
    }

    @Test
    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.(예 : “1”)")
    void inputOnlyOne() {
        Assertions.assertThat(strategy.split("1")).containsOnly(new Number(1));
    }
}
