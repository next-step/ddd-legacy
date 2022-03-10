package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class TokenFactoryTest {

    private TokenFactory tokenFactory;

    @BeforeEach
    void setUp() {
        tokenFactory = new TokenFactory();
    }

    @DisplayName("text가 null이거나 empty라면 빈 토큰을 반환한다")
    @NullAndEmptySource
    @ParameterizedTest
    void nullOrEmptyText(final String text) {
        // given

        // when
        final List<String> result = tokenFactory.createTokens(text);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("커스텀 구분자를 정의하지 않은 text라면 ,이나 :로 구분된 토큰을 반환한다")
    @ValueSource(strings = {"1,2,3", "1:2:3", "1,2:3"})
    @ParameterizedTest
    void defaultToken(final String text) {
        // given

        // when
        final List<String> result = tokenFactory.createTokens(text);

        // then
        assertThat(result).containsExactly("1", "2", "3");
    }

    @DisplayName("커스텀 구분자를 정의한 않은 text라면 그것으로 구분된 토큰을 반환한다")
    @ValueSource(strings = {"//+\n1+2+3", "//-\n1-2-3", "//a\n1a2a3"})
    @ParameterizedTest
    void customToken(final String text) {
        // given

        // when
        final List<String> result = tokenFactory.createTokens(text);

        // then
        assertThat(result).containsExactly("1", "2", "3");
    }
}