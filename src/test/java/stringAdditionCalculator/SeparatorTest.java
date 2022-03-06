package stringAdditionCalculator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SeparatorTest {
	@DisplayName("커스텀 구분자는 2글자를 넘을 수 없다")
	@Test
	void the_length_of_custom_separator_can_not_exceed_over_two_characters() {
		String inputWithLongSeparator = "//ab\n1ab1ab2ab1ab3";

		assertThatThrownBy(() -> Separator.split(inputWithLongSeparator)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("커스텀 구분자가 비어있으면 에러가 발생한다")
	@Test
	void if_custom_separator_is_empty_then_error_occurs() {
		String inputWithEmptySeparator = "//\n11213";

		assertThatThrownBy(() -> Separator.split(inputWithEmptySeparator)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("커스텀 구분자에는 숫자를 사용할 수 없다")
	@Test
	void number_type_is_not_allowed_for_custom_separator() {
		String inputWithCustomSeparator = "//1\n11213";

		assertThatThrownBy(() -> Separator.split(inputWithCustomSeparator)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("커스텀 구분자로 입력값을 자른다")
	@Test
	void separate_by_custom_separator() {
		String inputWithCustomSeparator = "//;\n1;2;3";

		String[] split = Separator.split(inputWithCustomSeparator);

		assertThat(split).hasSize(3);
		assertThat(split[0]).isEqualTo("1");
		assertThat(split[1]).isEqualTo("2");
		assertThat(split[2]).isEqualTo("3");
	}

	@DisplayName("쉼표와 콜론으로 입력값을 자른다")
	@Test
	void separate_by_comma_or_colon() {
		String inputWithCommaOrColon = "a:b,c";

		String[] split = Separator.split(inputWithCommaOrColon);

		assertThat(split).hasSize(3);
		assertThat(split[0]).isEqualTo("a");
		assertThat(split[1]).isEqualTo("b");
		assertThat(split[2]).isEqualTo("c");
	}

	@DisplayName("쉼표로 입력값을 자른다")
	@Test
	void separate_by_comma() {
		String inputWithComma = "a,b,c";
		String[] split = Separator.split(inputWithComma);

		assertThat(split).hasSize(3);
		assertThat(split[0]).isEqualTo("a");
		assertThat(split[1]).isEqualTo("b");
		assertThat(split[2]).isEqualTo("c");
	}

	@DisplayName("콜론으로 입력값을 자른다")
	@Test
	void separate_by_colon() {
		String inputWithColon = "a:b:c";
		String[] split = Separator.split(inputWithColon);

		assertThat(split).hasSize(3);
		assertThat(split[0]).isEqualTo("a");
		assertThat(split[1]).isEqualTo("b");
		assertThat(split[2]).isEqualTo("c");
	}
}
