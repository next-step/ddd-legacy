package calculator;

import calculator.tokenizer.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TokenizerFactory를 테스트한다.")
public class TokenizerTest {

    @DisplayName(",혹은 : 구분자가 있으면 DefaultTokenizer 반환한다.")
    @ParameterizedTest(name = "{0} 구분하는 DefaultTokenizer")
    @ValueSource(strings = {"1,2","1:2"})
    void tokenizerFactoryDefault(final String text) {
        TokenizerFactory factory = new TokenizerFactory(text);
        Tokenizer tokenizer = factory.createTokenizer();
        assertThat(tokenizer).isInstanceOf(DefaultTokenizer.class);
    }

    @DisplayName("//와 \n 사이의 문자가 있으면 CustomTokenizer를 반환한다.")
    @ParameterizedTest(name = "{0} 구분하는 CustomTokenizer")
    @ValueSource(strings = {"//;\n1;2;3"})
    void tokenizerFactoryCustom(final String text) {
        TokenizerFactory factory = new TokenizerFactory(text);
        Tokenizer tokenizer = factory.createTokenizer();
        assertThat(tokenizer).isInstanceOf(CustomTokenizer.class);
    }

    @DisplayName("문자가 null이나 비어있으면 EmptyTokenizer를 반환한다.")
    @ParameterizedTest(name = "{0}인 경우, EmptyTokenizer 반환")
    @NullAndEmptySource
    void tokenizerFactoryEmtpy(final String text) {
        TokenizerFactory factory = new TokenizerFactory(text);
        Tokenizer tokenizer = factory.createTokenizer();
        assertThat(tokenizer).isInstanceOf(EmptyTokenizer.class);
    }

    @DisplayName("일반 숫자형 문자인 경우 NonTokenizer를 반환한다.")
    @ParameterizedTest(name = "{0}인 경우, NonTokenizer 반환")
    @ValueSource(strings = {"1","2"})
    void tokenizerFactoryNone(final String text) {
        TokenizerFactory factory = new TokenizerFactory(text);
        Tokenizer tokenizer = factory.createTokenizer();
        assertThat(tokenizer).isInstanceOf(NoneTokenizer.class);
    }

    @DisplayName("DefaultTokenizer로 ,혹은 :를 구분자로 문자를 분리한다.")
    @ParameterizedTest(name = "{0} 문자열 분리")
    @ValueSource(strings = {"1,2","1:2"})
    void defaultTokenizer(final String text) {
        Tokenizer tokenizer = new DefaultTokenizer(text);
        assertThat(tokenizer.split()).contains("1","2");
    }

    @DisplayName("CustomTokenizer로 //와 \n 사이의 문자를 구분자로 문자를 분리한다.")
    @ParameterizedTest(name = "{0} 문자열 분리")
    @ValueSource(strings = {"//;\n1;2;3"})
    void customTokenizer(final String text) {
        Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (matcher.find()) {
            Tokenizer tokenizer = new CustomTokenizer(matcher);
            assertThat(tokenizer.split()).contains("1","2","3");
        }
    }

}
