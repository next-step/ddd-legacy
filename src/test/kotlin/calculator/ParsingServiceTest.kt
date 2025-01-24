package calculator

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatRuntimeException
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.stream.Stream

class ParsingServiceTest {

    @ParameterizedTest
    @ArgumentsSource(DefaultDelimiterArgumentsProvider::class)
    fun `기본 구분자와 숫자 파싱 성공`(target: String, expected: List<NonNegativeNumber>) {
        val input = InputString.of(target)
        assertThat(input.toNonNegativeNumbers()).isEqualTo(expected)
    }

    @ParameterizedTest
    @ArgumentsSource(CustomDelimiterArgumentsProvider::class)
    fun `커스텀 구분자와 숫자 파싱 성공`(target: String, expected: List<NonNegativeNumber>) {
        val input = InputString.of(target)
        assertThat(input.toNonNegativeNumbers()).isEqualTo(expected)
    }

    @ParameterizedTest
    @ValueSource(strings = ["""//;\n0;2,3""", """//;\n1:0;3"""])
    fun `커스텀 구분자와 기본 구분자를 섞어 사용할 수 없음`(target: String) {
        val input = InputString.of(target)
        assertThatRuntimeException().isThrownBy { input.toNonNegativeNumbers() }
    }
}

class DefaultDelimiterArgumentsProvider : ArgumentsProvider {

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> =
        Stream.of(
            Arguments.of(
                "0,1,2",
                listOf(0, 1, 2).toNonNegativeNumbers()
            ),
            Arguments.of(
                "1,2:3",
                listOf(1, 2, 3).toNonNegativeNumbers()
            ),
        )
}

class CustomDelimiterArgumentsProvider : ArgumentsProvider {

    override fun provideArguments(context: ExtensionContext): Stream<out Arguments> =
        Stream.of(
            Arguments.of(
                """//;\n0;1;2""",
                listOf(0, 1, 2).toNonNegativeNumbers()
            ),
            Arguments.of(
                """//;\n1;20;3""",
                listOf(1, 20, 3).toNonNegativeNumbers()
            ),
        )
}
