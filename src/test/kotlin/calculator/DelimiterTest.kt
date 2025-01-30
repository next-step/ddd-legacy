package calculator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class DelimiterTest {

    @ParameterizedTest
    @ValueSource(strings = ["0,1,2", "1,2,3"])
    fun `기본 구분자로 생성 성공`(target: String) {
        assertThat(Delimiter(target).regex.toString()).isEqualTo("[,:]")
    }

    @ParameterizedTest
    @CsvSource("""//;\n0;2;3|;""", """//;\n1;0;3|;""", """//.\n1.2.3|.""", delimiter = '|')
    fun `커스텀 구분자로 생성 성공`(target: String, expected: String) {
        assertThat(Delimiter(target).regex.toString()).isEqualTo(Regex.escape(expected))
    }
}
