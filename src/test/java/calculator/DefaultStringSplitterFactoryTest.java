package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DefaultStringSplitterFactoryTest {

    private DefaultStringSplitterFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DefaultStringSplitterFactory();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("빈 문자열 또는 null 전달 시 DefaultStringSplitter 인스턴스를 반환한다.")
    void empty(final String input) {
        assertThat(factory.create(input)).isInstanceOf(DefaultStringSplitter.class);
    }

    @ParameterizedTest
    @DisplayName("커스텀 문자열 구분자 포맷이 아닐 경우 DefaultStringSplitter 인스턴스를 반환한다")
    @ValueSource(strings = {
        "invalid",
        "1,2",
        "test"
    })
    void notCustomFormat(final String input) {
        assertThat(factory.create(input)).isInstanceOf(DefaultStringSplitter.class);
    }

    @ParameterizedTest
    @DisplayName("//'와 '\n' 사이에 커스텀 구분자를 지정하고, 해당 구분자로 구분된 문자열 전달 시 CustomStringSplitter 인스턴스를 반환한다.")
    @ValueSource(strings = {
        "//;\n1;2",
        "//.\n5.6"
    })
    void customFormat(final String input) {
        assertThat(factory.create(input)).isInstanceOf(CustomStringSplitter.class);
    }
}