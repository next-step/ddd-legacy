package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SplitterTest {

	@Test
	void split() {
		Splitter splitter = Splitter.from(",", ":");

		assertThat(splitter.split("1,2:3")).containsExactly("1", "2", "3");
	}

	@Test
	void add() {
		assertThat(Splitter.from(",").addDelimiter(":"))
			.extracting("delimiters")
			.asList()
			.containsExactly(",", ":");
	}
}
