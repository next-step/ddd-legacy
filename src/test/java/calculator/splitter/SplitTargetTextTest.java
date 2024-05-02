package calculator.splitter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("SplitTargetText")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SplitTargetTextTest {

    @ParameterizedTest
    @NullAndEmptySource
    void null_또는_empty한_문자열로_SplitTargetText를_생성하면_IllegalArgumentException를_던진다(String text) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new SplitTargetText(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {"가,나,다", "가,나;다", "가;나;다"})
    void SplitTargetText를_구분자로_분리한다(String text) {
        SplitTargetText splitTargetText = new SplitTargetText(text);

        assertThat(splitTargetText.split(",|;")).contains("가", "나", "다");
    }
}